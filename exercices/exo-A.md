Exercise A: a rule that stops a sale (30 min)

Goal: add a guard and prove the rollback.

In GitHub Desktop, create a branch exercice-A (Current Branch, then New Branch), so you can always go back.
Open socobis-ejb/src/java/vente/Vente.java, method createObjectBatch (about line 807). Find the line printing "APRES CHECK DETAIL NULL" and add right after it:
java
if (listeFille.length > 5) {
    throw new Exception("Maximum 5 articles par vente");
}
Run ./deploy.sh, then hard-refresh the browser (Cmd+Shift+R).
Note the row counts (the select count(*) query you used before), then try a sale with 6 different products.

Expected: an alert with your message (the catch in apresVenteDirecteOptim.jsp shows e.getMessage()), and no new row in vente, vente_details or mouvementcaisse.

What you learn: where a business rule lives, the deploy loop, and that the transaction really rolls back. If a sale with exactly 5 or 6 lines behaves differently than you expect, tell me. That is an observation about how lines are counted.