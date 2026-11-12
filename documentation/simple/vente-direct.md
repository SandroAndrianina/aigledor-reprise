The experiment worked, and the code explains it. This is your first complete data flow, verified.

**Verified by the database:** one sale with one product gave **+1** in `vente`, **+1** in `vente_details`, and **+1** in `mouvementcaisse`. Your sale `VNT002952062711` has client `CLI000576`, which the doc says is the default "Client Divers", and the point `PNTB0001`.

**Verified by the code** (`VentePaiement.createObjectBatch`): everything is in one transaction, and the steps run in this order:

```
Click "Payer"
 → apresVenteDirecteOptim.jsp   rebuilds VentePaiement + VenteDetails[] from the form
 → UserEJBBean.createObjectMultipleBatch   opens the transaction
 → VentePaiement.createObjectBatch:
     1. insert the sale and its lines        (vente, vente_details)
     2. insert the cash movement             (mouvementcaisse)
     3. insert the stock movement            (mvtstock, I haven't checked this table)
     4. validate the sale (accounting, forecasts)
     5. validate the cash movement
     6. validate the stock movement (checks stock is available)
     7. one commit; any error rolls everything back
 → ticket PDF   (this is the step that failed, after the save)
```

**Why this matters for the exam:** a sale is not "one insert". It is a sale, plus cash, plus stock, plus validations, all or nothing. If stock is insufficient, step 6 fails and **nothing** is saved.

**Not verified:** what `etat = 11` means. It's probably "validated" because validation runs, but that's an inference.

**Do now (2 minutes):** copy the diagram above into your notes file. That's principle 3, your own map.

**Next step I suggest:** open `Vente.createObjectBatch` (the `super` call in step 1) to see how the SQL `INSERT` of the sale and its lines is built. That is the ORM part you said was a mystery. Tell me when you're ready.