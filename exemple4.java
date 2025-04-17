/**
* Met à jour les différents statuts et crée les paiements selon le type de
* règlement
*
* @param dossierDeces le dossier décès à mettre à jour et pour lequel il faut
*                     créer les paiements
*/
private boolean miseAJourStatutsEtCreePaiements(final Pass pass, final DossierDeces dossierDeces) {
    boolean paimentEffectue = false;
    for (final DossierDecesContrat contrat : dossierDeces.getDossierDecesContrats()) {
        for (final AffectationBeneficiaireContrat affectation : contrat.getAffectations()) {
            if (affectation.getStatut()) {
                final DossierDecesBeneficiaire benef = affectation.getBeneficiaire();
                // benef.setPiecesDemandees(pieceDemandeService.listDemandePieceByEntite(pass,
                // "DECES", benef.getId() == null ? 0 : benef.getId(), false));
                // Si règlement ou transfert en cours, on change les statuts
                // TODO remettre le ! JNI
                if (EnumStatutDossier.REGLE.getCode().equals(benef.getStatutDossierBeneficiaire())
                        && !EnumStatutDossier.TRANSFERT_CDC.getCode().equals(benef.getStatutDossierBeneficiaire())
                        && !EnumStatutDossier.TRANSFERT_ETAT.getCode().equals(benef.getStatutDossierBeneficiaire())
                        && benef.getModeReglement() != null
                        && affectation.getBeneficiaire().getPaiementEffectue() == 0) {

                    // si en rente
                    if (EnumModeReglement.REEMPLOI.getLibelle().equalsIgnoreCase(benef.getModeReglement())) {
                        // TODO : appel du back end des RENTE
                        /*
                         * DossierDecesCaller dossierDecesDao = new DossierDecesCaller();
                         * dossierDecesDao .liaisonRente(createContratPrestion(dossierDeces,
                         * affectation)); result = true;
                         */
                    }
                    // si en chèque
                    if (EnumModeReglement.CHEQUE.getLibelle().equalsIgnoreCase(benef.getModeReglement())) {
                        createTresoreriePLSQL(affectation, EnumModeReglement.CHEQUE.getCode());
                    }
                    // si en virement
                    if (EnumModeReglement.VIREMENT.getLibelle().equalsIgnoreCase(benef.getModeReglement())) {
                        createPaiementPLSQL(affectation);
                    }

                    affectation.getBeneficiaire().setPaiementEffectue(1);
                    paimentEffectue = true;

                    // mise à jour des statuts bénéficiaire
                    if (EnumStatutDossier.TRANSFERT_CDC.getCode().equals(benef.getTypeSortie())) {
                        affectation.getBeneficiaire()
                                .setStatutDossierBeneficiaire(EnumStatutDossier.TRANSFERT_CDC.getCode());
                    } else if (EnumStatutDossier.TRANSFERT_ETAT.getCode().equals(benef.getTypeSortie())) {
                        affectation.getBeneficiaire()
                                .setStatutDossierBeneficiaire(EnumStatutDossier.TRANSFERT_ETAT.getCode());
                    } else {
                        affectation.getBeneficiaire()
                                .setStatutDossierBeneficiaire(EnumStatutDossier.REGLE.getCode());
                    }

                    affectation.getBeneficiaire().setDateReglement(new Date());
                }

                if (checkStatutDossier(benef)) {
                    affectation.setStatutDossier(EnumStatutDossier.REGLE.getCode());
                }
            }
        }
    }
    return paimentEffectue;
}