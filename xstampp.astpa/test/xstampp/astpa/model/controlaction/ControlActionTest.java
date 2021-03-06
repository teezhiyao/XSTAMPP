/*******************************************************************************
 * Copyright (C) 2018 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology, Software Engineering Group University of Stuttgart, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology, Software Engineering Group University of Stuttgart, Germany - initial API and implementation
 ******************************************************************************/
package xstampp.astpa.model.controlaction;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import xstampp.astpa.model.controlaction.interfaces.UnsafeControlActionType;

/**
 * Test class for control action
 * 
 * @author Fabian Toth
 * 
 */
public class ControlActionTest {
	
	/**
	 * Test of a control action
	 * 
	 * @author Fabian Toth
	 * 
	 */
	@Test
	public void controlActionTest() {
		// check parameterized constructor
		ControlAction controlAction = new ControlAction("Test", "Test description", 1);
		Assert.assertEquals("Test", controlAction.getTitle());
		Assert.assertEquals("Test description", controlAction.getDescription());
		Assert.assertEquals(1, controlAction.getNumber());
		
		// add a unsafe control action
		UUID uca1 = controlAction.addUnsafeControlAction(0,"Test NotGiven", UnsafeControlActionType.NOT_GIVEN);
		UUID uca2 =
			controlAction.addUnsafeControlAction(1,"Test GivenIncorrectly", UnsafeControlActionType.GIVEN_INCORRECTLY);
		UUID uca3 =
			controlAction.addUnsafeControlAction(2,"Test StoppedTooSoon", UnsafeControlActionType.STOPPED_TOO_SOON);
		UUID uca4 = controlAction.addUnsafeControlAction(3,"Test WrongTiming", UnsafeControlActionType.WRONG_TIMING);
		Assert.assertEquals(4, controlAction.getUnsafeControlActions().size());
		Assert.assertEquals(1, controlAction.getUnsafeControlActions(UnsafeControlActionType.GIVEN_INCORRECTLY).size());
		Assert.assertEquals(1, controlAction.getUnsafeControlActions(UnsafeControlActionType.NOT_GIVEN).size());
		Assert.assertEquals(1, controlAction.getUnsafeControlActions(UnsafeControlActionType.STOPPED_TOO_SOON).size());
		Assert.assertEquals(1, controlAction.getUnsafeControlActions(UnsafeControlActionType.WRONG_TIMING).size());
		
		// remove the unsafe control actions
		Assert.assertTrue(controlAction.removeUnsafeControlAction(uca4));
		Assert.assertTrue(controlAction.removeUnsafeControlAction(uca3));
		Assert.assertTrue(controlAction.removeUnsafeControlAction(uca2));
		Assert.assertTrue(controlAction.removeUnsafeControlAction(uca1));
		
		// Try to remove a unsafe control action the second time
		Assert.assertFalse(controlAction.removeUnsafeControlAction(uca4));
		
		// check empty constructor
		controlAction = new ControlAction();
		controlAction.setTitle("Test");
		Assert.assertEquals("Test", controlAction.getTitle());
		controlAction.setDescription("Test description");
		Assert.assertEquals("Test description", controlAction.getDescription());
		UUID id = UUID.randomUUID();
		controlAction.setId(id);
		Assert.assertEquals(id, controlAction.getId());
		controlAction.setNumber(5);
		Assert.assertEquals(5, controlAction.getNumber());
	}
}
