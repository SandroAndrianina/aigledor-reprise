The error was a **leftover from the Oracle to PostgreSQL migration**, in a database view.

- **What the view does:** it builds `datyheure` (date + time of the sale) by gluing the date and the time as text, then reading that text with an Oracle-style format.
- **Why Oracle accepted it:** a date turned into text gives only the day, so gluing the time after it worked.
- **Why PostgreSQL refused it:** the date already carries `00:00:00`, so the glued text became `2026-10-30 00:00:00 06:27:11`, which isn't a valid date and time.
- **Why you saw "fichier PDF est vide":** the ticket code catches every error, prints it, and returns `null`. The JSP then only sees `null`, so the real SQL error never reached your screen.
- **The fix:** add the time to the date (`date + time`) instead of gluing text.

The sale itself was never affected, because it is saved before the ticket is generated.

**The lesson:** when a message is vague ("vide", "not found"), look for the code that swallowed the real error. It's the same pattern as the login bug (`GetConn` returned `null`).