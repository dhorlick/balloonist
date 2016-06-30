package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.AbstractStem;

public class RigidityEdit extends StatefulMultipleEdit
{
    public RigidityEdit(double newRigidity)
    {
	    super(MENU_TEXT.getString("changeRigidityLabel"), new Double(newRigidity));
    }

	public Object setState(Object thing, Object value)
    {
		if (thing instanceof AbstractStem && value instanceof Number)
		{
			AbstractStem thingAsStem = (AbstractStem) thing;
			Number valueAsNumber = (Number) value;
			
			double oldRigidity = 1.0 - thingAsStem.getBendiness();
			
			final double newBendiness = 1.0 - valueAsNumber.doubleValue();
			// System.out.println("re new bendiness = "+newBendiness);
			thingAsStem.setBendiness(newBendiness);
			
			return new Double(oldRigidity);
		}
		
		return NO_EFFECT;
    }
}
