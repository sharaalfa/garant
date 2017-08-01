package ru.garant.mdp;

import static org.junit.Assert.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class MainTest {
	
    @Autowired
    private SessionFactory sessionFactory;
	private Session session;
	private Transaction tx;
    
    @Before
    public void before () {
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		session.setFlushMode(FlushMode.COMMIT);
    }
    
    @After
    public void after () {
    	tx.commit();
    	session.close();
    }
    
    @Test
    public void shouldHaveASessionFactory() {
        assertNotNull(sessionFactory);
    }
	/**
	 * вместо одного значения, нужно возвращать список значений, целое поле с методом list().
	 */
	@Test
	public void shouldFindModelByField () {
		Model model = createRandomModel();
		session.save(model);

		model.setField0("TEST1");
		session.save(model);
			
		assertTrue(null != session.createSQLQuery("select id from model where field0 = '" + model.getField0() + "'").list());
	}
	/**
	 * здесь нужно уменьшить количество процессов на кучу. Два варианта - либо уменьшить ручным способом, т.е. при помощи HashMap.
	 * Есть другой способ - через хэширование c HashMap более профессионально, например, при помощи Apache Ignite.
	 * Я бы сделал так, но придется перебрать все зависимости.
	 * Можно быть еще создать многопоточность.)
	 *
	 */
	
	@Test
	//@Ignore
	public void shouldNotCrashWithOutOfMemory () {
		try {
			HashMap<Integer, HashMap<Integer, Transaction>> map2 = null;
			int count = 0;
			for (int j = 0; j < 10; j++){

				HashMap<Integer, Transaction> map = null;
				count++;
				for (int i = 1; i <= count*100000; i++) {
					Model model = createRandomModel();
					session.save(model);

					if (i % 10000 == 0) {
						System.out.println ("processed: " + i);

						tx.commit();
						tx = session.beginTransaction();
						map.put(i, tx);
					}
				}
				map2.put(j, map);
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private Model createRandomModel () {
		Model ret = new Model ();
		ret.setField0(RandomStringUtils.randomAlphanumeric(200));
		ret.setField1(RandomStringUtils.randomAlphanumeric(200));
		ret.setField2(RandomStringUtils.randomAlphanumeric(200));
		ret.setField3(RandomStringUtils.randomAlphanumeric(200));
		ret.setField4(RandomStringUtils.randomAlphanumeric(200));
		ret.setField5(RandomStringUtils.randomAlphanumeric(200));
		ret.setField6(RandomStringUtils.randomAlphanumeric(200));
		ret.setField7(RandomStringUtils.randomAlphanumeric(200));
		ret.setField8(RandomStringUtils.randomAlphanumeric(200));
		ret.setField9(RandomStringUtils.randomAlphanumeric(200));
		return ret;
	}

}
