/**
 * @author jonh_doe
 */
@Service(AdhesionService.SID)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdhesionService extends AbstractService<Adhesion> {
  /**
   * Identifiant du service
   */
  public static final String         SID = "adhesionService";

  @Autowired
  @Qualifier("adhesionDao")
  protected IDaoFoundation<Adhesion> dao;

  @Override
  protected IDaoFoundation<Adhesion> getDao() {
    // TODO Auto-generated method stub
    return dao;
  }

  public void setDao(IDaoFoundation<Adhesion> dao) {
    this.dao = dao;
  }

  /**
   * @param id
   * @return
   */
  public Adhesion getAdhesion(Integer id) {
    return ((AdhesionDao) dao).get(id);
  }
}