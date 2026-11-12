Exercise B: a new field, end to end (1 to 2 hours, the important one)

Goal: add a “cashier note” to the sale and make it travel from the screen to the database. This exercises the framework’s core mechanism.

New branch exercice-B.
Database:
bash
docker exec aigledor-pg psql -U postgres -d aigledor -c "ALTER TABLE vente ADD COLUMN notecaisse varchar(255)"
Java: in Vente.java, add the field private String notecaisse; with its getter and setter (getNotecaisse, setNotecaisse), next to the other fields.
Screen: in vente-saisie-directe-apj.jsp, inside the form #venteForm, add <input type="text" name="notecaisse" id="notecaisse">.
Deploy, make a sale with a note, then check:
bash
docker exec aigledor-pg psql -U postgres -d aigledor -c "select id, notecaisse from vente order by daty desc, id desc limit 3"

Expected: your note appears. If it doesn’t, that’s the lesson: find which link of the chain broke (form field, object field, column) using the debugging method. Tell me what you see, and we diagnose it together.

My hypothesis, unverified: the name must be identical at the 3 levels (form input, Java field, column). You are testing it.