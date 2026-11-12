# Map of one feature: "vente directe" (POS sale), end to end

Project: Aigle d'Or (modules `socobis-ejb` = backend, `socobis-war` = web). Framework: APJ (`build-file/lib/apj-core.jar`).
All paths are relative to the project root. `EJB/` = `socobis-ejb/src/java/`, `WAR/` = `socobis-war/web/`.

**Legend:** ✅ verified in the code or in the database. 🔶 assumed (from names or comments), to verify. Line numbers are approximate (the framework ones come from a decompiled copy, so they are not the original lines).

---

## 0. The whole flow in 5 sentences

1. The page posts the sale form to `apresVenteDirecteOptim.jsp`.
2. The JSP rebuilds a `VentePaiement` (the mother) with its `VenteDetails[]` (the children) and the payments.
3. `UserEJBBean.createObjectMultipleBatch` opens **one transaction**.
4. `VentePaiement.createObjectBatch` runs the steps: sale (with its rules), cash movement, stock movement, then the validations.
5. If any rule fails, **everything is rolled back**; if not, one commit. Then the ticket PDF is generated (after the save, so a PDF error does not cancel the sale).

```
Login (index.jsp -> pages/testLogin.jsp)  => session attribute "u" (UserEJB)
        |
vente-saisie-directe-apj.jsp   (screen, builds the form)
        | POST (acte=insert)
apresVenteDirecteOptim.jsp     (form handler)
        |  u.createObjectMultipleBatch(...)
UserEJBBean.createObjectMultipleBatch   (transaction)
        |  createObjectBatch(mere, c)
VentePaiement.createObjectBatch   (the recipe, 7 steps)
   |-- Vente.createObjectBatch          (sale rules, then write)
   |-- genererEncaissementBatch         (cash movement objects)
   |-- Vente.genererMvtStock            (stock movement objects)
   |-- Vente.validerObjectBatch         (validation, accounting, forecasts)
   |-- cash + stock validations
        |  super.createObjectBatch
ClassMere / ClassMAPTable  (ORM, in apj-core.jar)  => INSERT queued with addBatch
        |  st.executeBatch() then commit
PostgreSQL: vente, vente_details, mouvementcaisse, ...
        |
flux_pdf.jsp -> ExportPDFDirect -> Jasper -> ticket PDF
```

---

## 1. The screen

| | |
|---|---|
| **Where** | `WAR/pages/vente/vente-saisie-directe-apj.jsp` + JS modules `WAR/assets/js/pos/pos-*.js` |
| **URL** | `/aigledor/pages/module.jsp?but=vente/vente-saisie-directe-apj.jsp` (`module.jsp` is the single entry page, `but=` = the JSP loaded inside it) ✅ |
| **Role** | Show the product search, the client search, the basket and the payment summary; build the form. |
| **In** | The logged-in user (session `u`), the user's point, store and cash registers. |
| **Out** | A form posted with `acte=insert`, `classe=vente.VentePaiement`, `classefille=vente.VenteDetails`, `colonneMere=idVente`, `nombreLigne`, `nomtable=Vente_Details`, `multiplePayment`, the payment amounts (`valeurEspece`, `montantMvola`, ...) and references (`refMvola`, ...). ✅ |
| **Next** | The submit button "Payer" posts to `apresVenteDirecteOptim.jsp`. |
| **Notes** | The form and the basket lines are generated in Java by `PageInsertMultiple` (HTML by string concatenation). JS modules compute totals and discounts. ✅ (doc). Field names must keep their `_N` index suffixes. |

## 2. The form handler

| | |
|---|---|
| **Where** | `WAR/pages/vente/apresVenteDirecteOptim.jsp`, block `acte == "insert"` (about lines 143 to 324) |
| **Role** | Receive the posted form, rebuild Java objects, call the service, then open the ticket. The file is a **dispatcher**: it contains many other `acte` blocks (update, delete, valider...) that you can ignore. ✅ |
| **In** | `request.getParameter(...)`: payment fields (lines 144 to 240), `classe`, `classefille`, `nombreLigne`, ... |
| **Out** | A `VentePaiement` `v` with its payments (`PaiementInfo[]`), the user's point, store, cash register; the children `cfille`. |
| **Next** | **Line 275:** `u.createObjectMultipleBatch(v, colonneMere, cfille)`. This is the one line that saves. Everything above prepares it. ✅ |
| **After** | If an id comes back: hidden iframe loads `flux_pdf.jsp?idVente=<id>`, then `print()`, then redirect to the POS page after 3 s. |

Payments: single mode = one `PaiementInfo`; `multiplePayment=true` = one per non-zero amount (cash, MVola, Orange, Airtel, cheque, TPE, bon d'achat, cadeaux).

## 3. The transaction

| | |
|---|---|
| **Where** | `EJB/user/UserEJBBean.java`, method `createObjectMultipleBatch` (about line 3402). `UserEJB.java` is only the interface. |
| **Role** | Open one connection with auto-commit off (one transaction), save the mother and the children, commit, or roll back on error. |
| **In** | The mother, the name of the foreign-key column (`idVente`), the children. |
| **Out** | The saved mother (its id, for example `VNT002952062711`). |
| **Next** | `createObjectBatch(mere, c)`, which reaches `VentePaiement.createObjectBatch`. |
| **Notes** | It sets state "created" and the user on the mother. It saves the children itself (`createObjectMultiple`) only if the mother's `getLiaisonFille()` is empty; otherwise the mother saves them (see step 8). 🔶 which path the sale takes. |

## 4. The recipe (outer layer)

| | |
|---|---|
| **Where** | `EJB/vente/VentePaiement.java`, method `createObjectBatch` (about lines 65 to 165) |
| **Class chain** | `VentePaiement extends Vente extends FactureCF extends ClassMere (framework)` ✅. Each layer redefines `createObjectBatch` and calls `super`. |
| **Role** | Orchestrate the whole sale in one transaction. |
| **Steps** | 1) `super.createObjectBatch` = insert sale + lines. 2) cash movement (`genererEncaissementBatch`, then `createObjectBatch`). 3) stock movement (`genererMvtStock`, then `createObjectBatch`). Then `executeBatch`. 4) `super.validerObjectBatch` (sale). 5) validate cash movement. 6) validate stock movement. 7) `executeBatch`, then `commit`. ✅ |
| **In** | The sale, its lines, `paiementInfos`, `montantRetourner`, the user's registers. |
| **Out** | The validated sale. |
| **Next** | Each step below. |

## 5. The sale rules (middle layer)

| | |
|---|---|
| **Where** | `EJB/vente/Vente.java`, method `createObjectBatch` (about line 807) |
| **Role** | Check the rules, then write the sale. |
| **Preparation** | Set the time; fetch the lines, the discount rules, the client. |
| **Guards** | Lines must exist; no product twice (`"Doublon pour l'article"`); discount applied per line; exchange invoice cannot exceed the original; stock check `checkEtatStockGroupe`. |
| **Action** | `super.createObjectBatch(u, st)` (goes to the ORM), then generate a barcode image. |
| **Next** | `ClassMere.createObjectBatch` (step 8). |

## 6. Payment becomes cash

| | |
|---|---|
| **Where** | `EJB/vente/VentePaiement.java`, method `genererEncaissementBatch` (about lines 200 to 330) |
| **Role** | **Build** (not save) the cash receipt. |
| **In** | `paiementInfos`, `montantRetourner`, the user's default cash register and the point's registers. |
| **Out** | A `MouvementCaisseMere` (credit = total TTC, `idorigine` = sale id) with one `MouvementCaisseFille` per payment. ✅ |
| **Logic** | `montantPaye = received - returned`. One child per payment. Register chosen by payment category (cash = default cash register; MVola, Orange, Airtel, cheque, TPE, bon d'achat, cadeaux = register of that category). Guard: if `montantPaye > total TTC + margin` then `"Le montant est supérieur"` (not for bon d'achat / cadeaux). Cash amount = given - change - "appoint". An extra "appoint" line is added for a small rounding gap. |
| **Next** | Returned to `createObjectBatch`, which saves it (`mvtCaisseMere.createObjectBatch`). |
| **DB** | Table `mouvementcaisse`: `idorigine` = sale id. ✅ (+1 row observed) |
| **Unknown** | How `getAppoint` is computed. 🔶 |

## 7. Stock

| | |
|---|---|
| **Where** | `EJB/vente/Vente.java`, method `genererMvtStock` (about line 3054) and `EJB/vente/VenteDetails.java`, method `genererMvtStockFille` (about line 871) |
| **Role** | **Build** the stock "exit" document. |
| **In** | The sale and its lines. |
| **Out** | A `MvtStock` (mother, one per sale, type `TPMVST000022` 🔶 probably "exit for a sale") with one `MvtStockFille` per line (`sortie` = quantity). |
| **Guard** | Only one stock movement per sale (`"Un mouvement de stock a déjà été généré"`). Store = `idMagasinVente`, else `idMagasin`. |
| **Next** | Saved at step 3 of the recipe, validated at step 6. |
| **DB** | Table `mvtstock`: `idvente` = sale id. 🔶 not checked. |

## 8. Validation

| | |
|---|---|
| **Where** | `EJB/vente/Vente.java`, method `validerObjectBatch` (about line 3152); mother version in `ClassMere` |
| **Role** | The "final inspection": validate the sale and trigger what must follow. |
| **Steps** | Per line: find the product, compute the cost price (`calculerRevient`), update the line. Then `super.validerObjectBatch` (marks the sale validated). Then `genererEcritureBatch` (accounting 🔶), `genererStockEngage` (a stock movement 🔶), `genererRistourne` (rebate, if any), `genererPrevisionBatch` (forecasts, by `estPrevu`). |
| **Out** | The sale with `etat = 11`. 🔶 11 is very probably "validated": check `ConstanteEtat.getEtatValider()` in the jar. |
| **Note** | I read the order and the names of steps 3 to 6, not the inner methods. |

## 9. The ORM (framework, `build-file/lib/apj-core.jar`)

**9a. Saving an object** (read from a decompiled copy: `cfr-decompiler ... --outputdir ~/dev/apj-src`)

| Where | Role |
|---|---|
| `bean/ClassMere.java` `createObjectBatch` (about line 104) | `construirePK` (id from a DB sequence), `super.createObjectBatch` (save the mother), then `createObjectFilleBatch` (children). ✅ |
| `bean/ClassMere.java` `createObjectFilleBatch` (about line 135) | For each child: write the mother's id in its link column (`getLiaisonFille()`), then `f.createObjectBatch`. ✅ |
| `bean/ClassMAPTable.java` `createObjectBatch` (about line 423) | `controlerBatch` (the object's checks), build the id if missing, `insertToTableWithHistoBatch`. ✅ |
| `bean/ClassMAPTable.java` `insertToTableWithHistoBatch` (about line 444) | Insert the object and a history row (`MapHistorique`, "Insertion par <user>"). ✅ |
| `bean/ClassMAPTable.java` `insertToTableBatch` (about line 462) | `getComandeInsert` builds the SQL text; `st.addBatch(sql)` queues it. **It does not run it.** ✅ The SQL runs at `st.executeBatch()`. |
| `bean/ClassMAPTable.java` `getComandeInsert` | How the `INSERT` text is built. 🔶 not read yet. |

**How columns meet fields (✅ verified):** the framework asks `INFORMATION_SCHEMA.COLUMNS` for the table's columns and matches them with the Java class fields **by name**, case-insensitive (`bean.ListeColonneTable`). A class says which table it maps to with `setNomTable("...")`.

**9b. Form to objects:** `affichage/PageInsertMultiple` (`getObjectAvecValeur`, `getObjectFilleAvecValeur`) rebuilds objects from the posted fields by reflection (index suffix `_N` = line N). 🔶 not read in detail.

## 10. The ticket PDF

| | |
|---|---|
| **Where** | `WAR/pages/vente/flux_pdf.jsp`, then `EJB/vente/ExportPDFDirect.java` (`ficheVenteDirect` line 181, then `genererPdfVente`) |
| **Role** | Produce the ticket PDF for one sale and send it to the browser. |
| **In** | `idVente`, the session user, the app's folder on disk. |
| **Out** | The PDF, or a text error. |
| **Data** | PostgreSQL views `VENTE_DIRECTE_CPL_PDF` and `VENTE_DETAILS_TICKET`, and the template `report/ticket_caisse.jasper` (JasperReports). ✅ |
| **Trap** | `genererPdfVente` catches every exception, prints it, and returns `null`. The JSP then shows "fichier PDF est vide". The real error is in the WildFly log. ✅ |

---

## 11. Database effect of one sale (observed)

One sale with one product, paid in cash:

| Table | Change |
|---|---|
| `vente` | +1 (state 11, client `CLI000576` = "Client Divers", point `PNTB0001`) ✅ |
| `vente_details` | +1 (one per product line) ✅ |
| `mouvementcaisse` | +1 (linked by `idorigine`) ✅ |
| `mvtstock`, history table, accounting, forecasts | expected 🔶 not checked |

## 12. Traps found while launching (for the record)

| Symptom | Real cause | Fix |
|---|---|---|
| Alert `Erreur makewhere : null` on the home page | `UtilDB.GetConn()` catches the connection error and returns `null`; the `NullPointerException` appears later in the framework. Real message in the log: `The authentication type 10 is not supported`: PostgreSQL 16 uses SCRAM, the old JDBC driver does not. | Switch the container to `md5` (`password_encryption`, `pg_hba.conf`). |
| Ticket: "fichier PDF est vide" | The view `vente_directe_cpl_pdf` built `datyheure` by gluing a date and a time as text (Oracle style); PostgreSQL rejects `2026-10-30 00:00:00 06:27:11`. | `CREATE OR REPLACE VIEW` with `v.daty::date + COALESCE(NULLIF(v.heuresaisie, ''), '00:00:00')::time AS datyheure`. |

**Reading technique, for any file:** role, in, out, next. Search for `u.`, `throw new Exception`, `getParameter`, `createObject`; skip `System.out.println` and commented code.
