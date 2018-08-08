package com.ram.kettle.tran;

import java.util.List;

import org.w3c.dom.Node;

import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.xml.XMLHandler;
import com.sun.corba.se.spi.ior.ObjectId;

/**
 * Defines a link between 2 steps in a transformation
 */
public class TransHopMeta implements Cloneable, Comparable<TransHopMeta> {
	private static Class<?> PKG = TransHopMeta.class; 

	public static final String XML_TAG = "hop";

	private StepMeta from_step;

	private StepMeta to_step;

	private boolean enabled;

	public boolean split = false;

	private boolean changed;

	private ObjectId id;

	public TransHopMeta(StepMeta from, StepMeta to, boolean en) {
		from_step = from;
		to_step = to;
		enabled = en;
	}

	public TransHopMeta(StepMeta from, StepMeta to) {
		from_step = from;
		to_step = to;
		enabled = true;
	}

	public TransHopMeta() {
		this(null, null, false);
	}

	public TransHopMeta(Node hopnode, List<StepMeta> steps)
			throws KettleException {
		try {
			from_step = searchStep(steps,
					XMLHandler.getTagValue(hopnode, "from")); //$NON-NLS-1$
			to_step = searchStep(steps, XMLHandler.getTagValue(hopnode, "to")); //$NON-NLS-1$
			String en = XMLHandler.getTagValue(hopnode, "enabled"); //$NON-NLS-1$

			if (en == null)
				enabled = true;
			else
				enabled = en.equalsIgnoreCase("Y"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"TransHopMeta.Exception.UnableToLoadHopInfo"), e); //$NON-NLS-1$
		}
	}

	public void setFromStep(StepMeta from) {
		from_step = from;
	}

	public void setToStep(StepMeta to) {
		to_step = to;
	}

	public StepMeta getFromStep() {
		return from_step;
	}

	public StepMeta getToStep() {
		return to_step;
	}

	private StepMeta searchStep(List<StepMeta> steps, String name) {
		for (StepMeta stepMeta : steps)
			if (stepMeta.getName().equalsIgnoreCase(name))
				return stepMeta;

		return null;
	}

	public Object clone() {
		try {
			Object retval = super.clone();
			return retval;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean equals(Object obj) {
		TransHopMeta other = (TransHopMeta) obj;
		if (from_step == null || to_step == null)
			return false;
		return from_step.equals(other.getFromStep())
				&& to_step.equals(other.getToStep());
	}

	/**
	 * Compare 2 hops.
	 */
	public int compareTo(TransHopMeta obj) {
		return toString().compareTo(obj.toString());
	}

	public ObjectId getObjectId() {
		return id;
	}

	public void setObjectId(ObjectId id) {
		this.id = id;
	}

	public void setChanged() {
		setChanged(true);
	}

	public void setChanged(boolean ch) {
		changed = ch;
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setEnabled() {
		setEnabled(true);
	}

	public void setEnabled(boolean en) {
		enabled = en;
		setChanged();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void flip() {
		StepMeta dummy = from_step;
		from_step = to_step;
		to_step = dummy;
	}

	public String toString() {
		String str_fr = (from_step == null) ? "(empty)" : from_step.getName(); //$NON-NLS-1$
		String str_to = (to_step == null) ? "(empty)" : to_step.getName(); //$NON-NLS-1$
		return str_fr
				+ " --> " + str_to + " (" + (enabled ? "enabled" : "disabled") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}
 
}
