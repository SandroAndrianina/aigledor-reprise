## Launch recap (your notes)

**Stack:** Java 8 (Zulu), Ant, WildFly 10.0.0.Final, PostgreSQL 16 in Docker.

1. **Database container:** `docker run -d --name aigledor-pg -e POSTGRES_PASSWORD=root -e POSTGRES_DB=aigledor -p 5433:5432 postgres:16`. Port 5433, because 5432 was already used on your Mac.
2. **Restore the dump:** `docker exec -i aigledor-pg psql -U postgres -d aigledor < aigledor0110.sql`. Result: 483 tables, 0 errors.
3. **Config:** `socobis-ejb/src/java/apj.properties` with url `jdbc:postgresql://localhost:5433/aigledor`, user `postgres`, password `root`, driver `org.postgresql.Driver`. Edit it **before** building.
4. **Tools:** `brew install --cask zulu@8`, `brew install ant`, and WildFly 10.0.0.Final unzipped in `~/dev`.
5. **Start WildFly (terminal 1):** `export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)`, then `~/dev/wildfly-10.0.0.Final/bin/standalone.sh`.
6. **Build and deploy (terminal 2),** from the project root: `ant -f build-aigledor.xml -Ddeploy.dir=$HOME/dev/wildfly-10.0.0.Final/standalone/deployments`.
7. **Open:** `http://localhost:8080/aigledor/`. Log in with `caisse` and a password from the `utilisateur` table (`pwduser`).

**The one real bug:** login to the database failed (SCRAM versus the old driver). Fix: the container switched to `md5` (`password_encryption`, `pg_hba.conf`). If you recreate the container, redo that fix.