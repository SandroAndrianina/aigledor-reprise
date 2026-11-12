docker exec aigledor-pg psql -U postgres -d aigledor -c "select * from mvtstock where idvente = 'VNT002952062711'"

docker exec aigledor-pg psql -U postgres -d aigledor -c "select * from point"