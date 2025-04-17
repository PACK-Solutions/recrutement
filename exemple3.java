/**
 * Gestion des échéanciers
 */
@Service(EcheancierService.SID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional
public class EcheancierService {
    public static final String SID = "EcheancierService";

    private final EcheancierDetailDao echeancierDetailDao;

    @Autowired
    public EcheancierService(final EcheancierDetailDao echeancierDetailDao) {
        this.echeancierDetailDao = echeancierDetailDao;
    }
    
    public ContratEcheancierCotisationDetail anuulerAnnulationEcheance(Long idEcheance, String loginConnexion) {
        ContratEcheancierCotisationDetail echeance = this.echeancierDetailDao.findEcheance(idEcheance);
        echeance.setFlagAttenteRegul(1);
        echeance.setUserUpdate(loginConnexion);
        echeance.setDateUpdate(LocalDateTime.now());
        return this.echeancierDetailDao.saveEcheance(echeance);
    }

    public ContratEcheancierCotisationDetail anuulerRepresentationEcheance(Long idEcheance, String loginConnexion) {
        ContratEcheancierCotisationDetail echeance = this.echeancierDetailDao.findEcheance(idEcheance);
        echeance.setStatut(0);
        echeance.setUserCancelled(loginConnexion);
        echeance.setDateCancelled(LocalDateTime.now());
        this.echeancierDetailDao.saveEcheance(echeance);

        List<ContratEcheancierCotisationDetail> listEcheances = this.echeancierDetailDao
                .findByEcheanceDateDebutModeRegleRepresented(echeance.getContratEcheancierCotisation().getId(),
                        echeance.getDateDebut(), echeance.getModeReglement());
        if(listEcheances != null) {
            Optional<ContratEcheancierCotisationDetail> echeanceAreprester = listEcheances.stream().max(Comparator.comparingLong(ContratEcheancierCotisationDetail::getId));
            
            if(echeanceAreprester.isPresent()) {
                echeanceAreprester.get().setUserUpdate(loginConnexion);
                echeanceAreprester.get().setDateUpdate(LocalDateTime.now());
                echeanceAreprester.get().setFlagAttenteRegul(1);
                this.echeancierDetailDao.saveEcheance(echeanceAreprester.get());
            }
        }
        return echeance;
    }
}