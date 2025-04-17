@Service(ContratService.SID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Slf4j
@Transactional(rollbackFor = ManyBusinessException.class)
public class ContratService {
    @Autowired
    private IContratDao iContratDao;
    @Autowired
    private IContratCollectifDao contratCollectifDao;
    @Autowired
    private IContratAffiliationDao contratAffiliationDao;
    @Autowired
    private IContratIndividuelDao contratIndividuelDao;

    public Long getIdContrat(final String referenceContratExterne) {
        if (referenceContratExterne != null) {
            final ContratCollectif contratCollectif =
                    this.contratCollectifDao.findOneByReferenceClient(referenceContratExterne);
            if (contratCollectif != null && contratCollectif.getContrat() != null
                    && contratCollectif.getContrat().getId() != null) {
                return contratCollectif.getContrat().getId();
            } else if (contratCollectif == null) {
                final ContratAffiliation contratAffiliation =
                        this.contratAffiliationDao.findOneByReferenceAffiliation(referenceContratExterne);
                if (contratAffiliation != null && contratAffiliation.getContrat() != null
                        && contratAffiliation.getContrat().getId() != null) {
                    return contratAffiliation.getContrat().getId();
                } else {
                    final ContratIndividuel contratIndividuel =
                            this.contratIndividuelDao.findOneByReferenceClient(referenceContratExterne);
                    if (contratIndividuel != null && contratIndividuel.getContrat() != null
                            && contratIndividuel.getContrat().getId() != null) {
                        return contratIndividuel.getContrat().getId();
                    }
                }
            }
        }
        return null;
    }

    public Contrat getContratForRejetBancaire(final Long idContrat) {
        final Contrat c = this.iContratDao.findOne(idContrat);
        if (c != null) {
            Hibernate.initialize(c.getContratCollectif());
            Hibernate.initialize(c.getContratAdhesions());
            Hibernate.initialize(c.getContacts());

            if (c.getContratAdhesions() != null) {
                Hibernate.initialize(c.getContratAdhesions().getReseauDistributionApporteur());
            }
        }
        return c;
    }
}