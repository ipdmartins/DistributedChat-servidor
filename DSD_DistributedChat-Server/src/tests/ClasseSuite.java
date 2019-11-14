package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.JUnit4TestAdapter;
import server.Control.ServerControl;

@RunWith(Suite.class)
@SuiteClasses({ TesteServer.class, ServerControl.class})
public class ClasseSuite {

	public static junit.framework.Test suite() {

		return new JUnit4TestAdapter(ClasseSuite.class); 

	}

}
