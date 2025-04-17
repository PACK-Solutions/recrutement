@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest({ RenteService.class, MessageUtils.class })
public class RenteServiceUnitTest {

    private RenteService renteService;
    private ArrerageService mockedArrerageService;
    private IValeurTauxDao mockedValeurTauxDao;
    private IDossierRenteDao mockedDossierRenteDao;
    private IProduitDao mockedProduitDao;
    private RefFractionImposableDao mockedRefFractionImposableDao;
    private RenteStrateService mockedRenteStrateService;
    private IRenteDao mockedRenteDao;
    private IEvenementsDao mockedEvenementsDao;
    private DateService dateService;
    private AjustementJourOuvre mockedAjustementJourOuvre;
    private RenteParamService mockedRenteParamService;
    private RentePointsService mockedRentePointsService;
    private DossierRenteService mockedDossierRenteService;
    private ContratAffiliationService mockedContratAffiliationService;
    private RoleService mockroleService;
    private AContratAdhesions mockAdhesion;
    private ProduitFiscaliteService mockedProduitFiscaliteService;
    private AContratAdhesionService mockedAContratAdhesionService;

    @Before
    public void setup() {
        mockedDossierRenteDao = mock(IDossierRenteDao.class);
        mockedProduitFiscaliteService = mock(ProduitFiscaliteService.class);
        mockedArrerageService = mock(ArrerageService.class);
        mockedValeurTauxDao = mock(IValeurTauxDao.class);
        mockedServiceSelector = mock(ServiceSelector.class);
        mockedProduitDao = mock(IProduitDao.class);
        mockedRenteStrateService = mock(RenteStrateService.class);
        mockedRenteDao = mock(IRenteDao.class);
        mockedRefFractionImposableDao = mock(RefFractionImposableDao.class);
        mockedAjustementJourOuvre = mock(AjustementJourOuvre.class);
        mockedRenteParamService = mock(RenteParamService.class);
        mockedRentePointsService = mock(RentePointsService.class);
        mockedDossierRenteService = mock(DossierRenteService.class);
        mockedContratAffiliationService = mock(ContratAffiliationService.class);
        mockedEvenementsDao = mock(IEvenementsDao.class);
        mockAdhesion = mock(AContratAdhesions.class);
        mockroleService = mock(RoleService.class);
        mockedAContratAdhesionService = mock(AContratAdhesionService.class);
        renteService = PowerMockito.spy(new RenteService(mockedRenteDao, mockedProduitDao, null, mockedDossierRenteDao, null, null, null, null, null, null, null, null, null, 
                mockedArrerageService, null, null, mockedRenteParamService, null, null, null, null, mockedRentePointsService, mockedServiceSelector,mockedProduitFiscaliteService, 
                mockedValeurTauxDao, null, null, null, null, mockedRefFractionImposableDao, null, mockedRenteStrateService, mockedDossierRenteService, null, null,
                null, null, null, mockedAContratAdhesionService, null, mockedContratAffiliationService, null, mockedEvenementsDao, mockroleService, null));
    }

    @Test
    public void testGivenRenteNouvelEtatClotureeAndDateDecesWhenTraitementDesuspensionRenteThenCallUpdateDateFicheArrerageDessuspensionSansReemission() throws AbstractException {
        // given
        final Pass pass = Mockito.mock(Pass.class);
        final String nouvelEtatRente = EnumRenteEtat.CLOTUREE.getCode();
        final Rente rente = Mockito.mock(Rente.class);
        final Integer renteId = new Random().nextInt();
        Mockito.doReturn(renteId).when(rente).getId();

        final Personne personne = Mockito.mock(Personne.class);
        Mockito.doReturn(personne).when(rente).getPersonne();
        final Date dateDeces = TimeTools.now();
        Mockito.doReturn(dateDeces).when(personne).getDateDeces();

        final DossierRente dossierRente = Mockito.mock(DossierRente.class);
        final Long dossierRenteCodeProduitEpargne = new Random().nextLong();
        Mockito.doReturn(dossierRente).when(mockedDossierRenteDao).findByIdRente(renteId);
        Mockito.doReturn(dossierRenteCodeProduitEpargne).when(dossierRente).getCodeProduitEpargne();

        final Arrerage arrerageRegu = new Arrerage();
        arrerageRegu.setEtat(EtatArrerage.BLOQUE);
        arrerageRegu.setMotifEtat(MotifEtatArrerage.BLOQUE_SUSPENDU);
        arrerageRegu.setTypeCreation(TypeCreationArrerage.DEREGU_REGU);

        final Arrerage arrerageReguAnnul = new Arrerage();
        arrerageReguAnnul.setEtat(EtatArrerage.BLOQUE);
        arrerageReguAnnul.setMotifEtat(MotifEtatArrerage.BLOQUE_SUSPENDU);
        arrerageReguAnnul.setTypeCreation(TypeCreationArrerage.DEREGU_ANNUL);

        final List<Arrerage> arrerageBloquesSuspendus = Arrays.asList(arrerageRegu, arrerageReguAnnul);
        Mockito.doReturn(arrerageBloquesSuspendus).when(mockedArrerageService).findByIdRente(renteId);

        Mockito.doNothing().when(renteService).updateDateFicheArrerageDessuspensionSansReemission(pass, rente,
                arrerageBloquesSuspendus);
        // when
        renteService.traitementDesuspensionRente(pass, rente, nouvelEtatRente);
        // then
        Mockito.verify(renteService, Mockito.never()).reemettreArrerages(any(Pass.class), any(DossierRente.class),
                any(Rente.class), anyList(), any(LocalDate.class));
        Mockito.verify(renteService, Mockito.never()).reemettreArreragesRegularisation(any(Pass.class),
                any(DossierRente.class), any(Rente.class), anyList(), anyList(), any(LocalDate.class));
        Mockito.verify(renteService).updateDateFicheArrerageDessuspensionSansReemission(pass, rente,
                arrerageBloquesSuspendus);
    }