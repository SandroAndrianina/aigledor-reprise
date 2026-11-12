 SELECT id,
    designation,
    idmagasin,
    idmagasinlib,
    daty,
    datyprevu,
    remarque,
    etat,
    adeclarer,
    etatlib,
    montanttotal,
    iddevise,
    idclient,
    idclientlib,
    montanttva,
    montantttc,
    montantttcar,
    montantpaye,
    montantreste,
    avoir,
    tauxdechange,
    montantrevient,
    margebrute,
    idreservation,
    aveccommission,
    idmodepaiement,
    referencefacture,
    idmodepaiementlib,
    montantdonne,
    montantretourner,
    heuresaisie,
    idecheancepaiement,
    echeancepaiementlib,
    idorigine,
    datyheure,
    nomclientdivers,
    idresponsablelib
   FROM ( SELECT v.id,
            v.designation,
            v.idmagasin,
            m.val AS idmagasinlib,
            v.daty,
            v.datyprevu,
            v.remarque,
            v.etat,
            v.adeclarer,
                CASE
                    WHEN v.etat = 1::numeric THEN 'CR&Eacute;&Eacute;E'::text
                    WHEN v.etat = 11::numeric THEN 'VIS&Eacute;E'::text
                    WHEN v.etat = 0::numeric THEN 'ANNUL&Eacute;E'::text
                    ELSE NULL::text
                END AS etatlib,
            v2.montanttotal,
            v2.iddevise,
            v.idclient,
            c.nom AS idclientlib,
            v2.montanttva,
            v2.montantttc,
            v2.montantttcar,
            (COALESCE(mv.credit, 0::numeric) - COALESCE(acg.montantpaye, 0::numeric))::numeric(30,2) AS montantpaye,
            (v2.montantttc - COALESCE(mv.credit, 0::numeric) - COALESCE(acg.resteapayer_avr, 0::numeric))::numeric(30,2) AS montantreste,
            COALESCE(acg.montantttc_avr, 0::numeric) AS avoir,
            v2.tauxdechange,
            v2.montantrevient,
            (v2.montantttcar - v2.montantrevient)::numeric(20,2) AS margebrute,
            v.idreservation,
                CASE
                    WHEN NOT cm.id IS NULL THEN 'Oui'::text
                    ELSE 'Non'::text
                END AS aveccommission,
            v.idmodepaiement,
            rf.reference AS referencefacture,
            mp.val AS idmodepaiementlib,
                CASE
                    WHEN v.montantdonne IS NULL OR v.montantdonne = 0::numeric THEN (COALESCE(mv.credit, 0::numeric) - COALESCE(acg.montantpaye, 0::numeric))::numeric(30,2)
                    ELSE v.montantdonne::numeric(30,2)
                END AS montantdonne,
            COALESCE(mv.montantretourner, 0::numeric) AS montantretourner,
            v.heuresaisie,
            v.idecheancepaiement,
            ep.desce AS echeancepaiementlib,
            v.idorigine,
            (v.daty::date + COALESCE(NULLIF(v.heuresaisie, ''), '00:00:00')::time) AS datyheure,
            v.nomclientdivers,
            u.nomuser AS idresponsablelib
           FROM vente v
             LEFT JOIN client c ON c.id::text = v.idclient::text
             LEFT JOIN ( SELECT p.id,
                    p.val,
                    p.desce,
                    p.id AS idpoint,
                    '-'::text AS idproduit,
                    NULL::text AS idtypemagasin,
                    11 AS etat
                   FROM point p) m ON m.id::text = v.idmagasin::text
             JOIN ventemontant v2 ON v2.id::text = v.id::text
             LEFT JOIN mvtcaissegroupefacture_pdf mv ON v.id::text = mv.idorigine::text
             LEFT JOIN avoirfclib_cpl_grp acg ON acg.idvente::text = v.id::text
             LEFT JOIN commission cm ON cm.idfacture::text = v.id::text
             LEFT JOIN modepaiement mp ON mp.id::text = v.idmodepaiement::text
             LEFT JOIN referencefacture rf ON rf.idfacture::text = v.id::text
             LEFT JOIN echeancepaiement ep ON ep.id::text = v.idecheancepaiement::text
             LEFT JOIN utilisateur u ON u.refuser = v.idresponsable::numeric) _0
  ORDER BY datyheure DESC NULLS LAST;
