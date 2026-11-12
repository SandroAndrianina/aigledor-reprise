#!/usr/bin/env bash
# deploy.sh - start the database, build, start WildFly, print the URL.
#
# Usage:  ./deploy.sh            build + run
#         ./deploy.sh --nobuild  run without rebuilding (no code change)
#         ./deploy.sh stop       stop WildFly
#
# Put this file in the project root (next to build-aigledor.xml).
set -euo pipefail

WILDFLY_HOME="$HOME/dev/wildfly-10.0.0.Final"
DB_CONTAINER="aigledor-pg"
APP="aigledor"
URL="http://localhost:8080/$APP/"
LOG="$HOME/dev/wildfly-$APP.log"
DEPLOY_DIR="$WILDFLY_HOME/standalone/deployments"
ARG="${1:-}"

cd "$(dirname "$0")"   # project root = the folder of this script
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"

# wait_for "what" seconds command...  -> waits until the command succeeds
wait_for() {
  local what="$1" secs="$2" i
  shift 2
  for ((i = 0; i < secs; i++)); do
    if "$@" >/dev/null 2>&1; then return 0; fi
    sleep 1
  done
  echo "ERROR: timeout while waiting for $what" >&2
  exit 1
}

if [ "$ARG" = "stop" ]; then
  "$WILDFLY_HOME/bin/jboss-cli.sh" --connect command=:shutdown
  echo "WildFly stopped."
  exit 0
fi

command -v ant >/dev/null || { echo "ERROR: ant not found (brew install ant)" >&2; exit 1; }

# 1. Database
if ! docker info >/dev/null 2>&1; then
  echo "Starting Docker Desktop..."
  open -a Docker
  wait_for "Docker" 120 docker info
fi
docker start "$DB_CONTAINER" >/dev/null
wait_for "the database" 30 docker exec "$DB_CONTAINER" pg_isready -U postgres -q
echo "Database ready."

# 2. Build (a marker file records the time before the build)
STAMP="$(mktemp)"
trap 'rm -f "$STAMP"' EXIT
BUILT=0
if [ "$ARG" != "--nobuild" ]; then
  echo "Building with Ant..."
  ant -q -f build-aigledor.xml -Ddeploy.dir="$DEPLOY_DIR"
  BUILT=1
fi

# 3. WildFly
if nc -z localhost 8080 2>/dev/null; then
  echo "WildFly already running."
else
  echo "Starting WildFly (log: $LOG)..."
  nohup "$WILDFLY_HOME/bin/standalone.sh" >"$LOG" 2>&1 &
  disown $! 2>/dev/null || true
fi

# 4. Wait for the deployment (only after a build: WildFly rewrites the marker)
if [ "$BUILT" = "1" ]; then
  deployed() {
    [ "$DEPLOY_DIR/$APP.war.deployed" -nt "$STAMP" ] || [ "$DEPLOY_DIR/$APP.war.failed" -nt "$STAMP" ]
  }
  wait_for "the deployment" 240 deployed
  if [ "$DEPLOY_DIR/$APP.war.failed" -nt "$STAMP" ]; then
    echo "ERROR: deployment failed. Last log lines:" >&2
    tail -n 40 "$LOG" >&2
    exit 1
  fi
fi

# 5. Wait until the page answers, then print the URLs
wait_for "the web app" 120 curl -sf -o /dev/null "$URL"

echo
echo "Ready. Click here:"
echo "   $URL"
echo "After login, the POS page:"
echo "   ${URL}pages/module.jsp?but=vente/vente-saisie-directe-apj.jsp"
echo
echo "Logs: tail -f $LOG     Stop: ./deploy.sh stop"
