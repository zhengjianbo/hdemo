/* Copyright (c) 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the GNU Lesser General Public License, Version 2.1. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.gnu.org/licenses/lgpl-2.1.txt. The Original Code is Pentaho 
 * Data Integration.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the GNU Lesser Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.*/

package com.ram.kettle.tran;

import java.util.ArrayList;
import java.util.List;

import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;

public class Trans {
	private static Class<?> PKG = Trans.class;

	public static final String REPLAY_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss"; //$NON-NLS-1$

	private TransMeta transMeta;

	private Trans parentTrans;

	private String mappingStepName;

	private List<StepMeta> steps;
 
	public Trans(TransMeta transMeta) {
		this.transMeta = transMeta;
	}

	public String getName() {
		if (transMeta == null)
			return null;

		return transMeta.getName();
	}

	public Trans(String name, String dirname, String filename)
			throws KettleException {
		try {
			transMeta = new TransMeta(filename);
		} catch (KettleException e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"Trans.Exception.UnableToOpenTransformation", name), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void prepareExcute() {
		// 初始化
		steps = transMeta.getTransHopSteps(false);
		for (int i = 0; i < steps.size(); i++) {
			StepMeta stepMeta = steps.get(i);
			stepMeta.init();
		}

	}

	public int nrSteps() {
		if (steps == null)
			return 0;
		return steps.size();
	}

	public List<StepInterface> findBaseSteps(String stepname) {
		List<StepInterface> baseSteps = new ArrayList<StepInterface>();

		if (steps == null)
			return baseSteps;
		for (int i = 0; i < steps.size(); i++) {
			StepMeta sid = steps.get(i);

			if (sid.getName().equalsIgnoreCase(stepname)) {
				baseSteps.add(sid);
			}
		}
		return baseSteps;
	}

	public TransMeta getTransMeta() {
		return transMeta;
	}

	/**
	 * @param transMeta
	 *            The transMeta to set.
	 */
	public void setTransMeta(TransMeta transMeta) {
		this.transMeta = transMeta;
	}

	/**
	 * @return Returns the steps.
	 */
	public List<StepMeta> getSteps() {
		return steps;
	}

	public String toString() {
		if (transMeta == null || transMeta.getName() == null)
			return getClass().getSimpleName();

		// See if there is a parent transformation. If so, print the name of the
		// parent here as well...
		//
		StringBuffer string = new StringBuffer();

		// If we're running as a mapping, we get a reference to the calling
		// (parent) transformation as well...
		//
		if (getParentTrans() != null) {
			string.append('[').append(getParentTrans().toString()).append(']')
					.append('.');
		}

		// When we run a mapping we also set a mapping step name in there...
		//
		if (!Const.isEmpty(mappingStepName)) {
			string.append('[').append(mappingStepName).append(']').append('.');
		}

		string.append(transMeta.getName());

		return string.toString();
	}

	/**
	 * Find the StepInterface (thread) by looking it up using the name
	 * 
	 * @param stepname
	 *            The name of the step to look for
	 * @param copy
	 *            the copy number of the step to look for
	 * @return the StepInterface or null if nothing was found.
	 */
	public StepInterface getStepInterface(String stepname, int copy) {
		if (steps == null)
			return null;

		// Now start all the threads...
		for (int i = 0; i < steps.size(); i++) {
			StepMeta sid = steps.get(i);
			if (sid.getName().equalsIgnoreCase(stepname)) {
				return sid;
			}
		}

		return null;
	}

	/**
	 * @return the parentTrans
	 */
	public Trans getParentTrans() {
		return parentTrans;
	}

	/**
	 * @param parentTrans
	 *            the parentTrans to set
	 */
	public void setParentTrans(Trans parentTrans) {
		this.parentTrans = parentTrans;
	}

	/**
	 * @return the name of the mapping step that created this transformation
	 */
	public String getMappingStepName() {
		return mappingStepName;
	}

	/**
	 * @param mappingStepName
	 *            the name of the mapping step that created this transformation
	 */
	public void setMappingStepName(String mappingStepName) {
		this.mappingStepName = mappingStepName;
	}

	public String getObjectName() {
		return getName();
	}

	public String getObjectCopy() {
		return null;
	}

}