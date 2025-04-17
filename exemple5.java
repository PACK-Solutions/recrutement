@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:*/spring-contextTest.xml")
@Transactional(propagation = Propagation.REQUIRED)
public class ContratDaoIntegrationTest {

    @Autowired
    private IContratDao iContratDao;

    @Test
    public void testGetContratById() {
        iContratDao.getOne(1l);

        Assert.assertTrue(true);
    }
}