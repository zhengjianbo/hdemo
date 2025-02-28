package com.ram.kettle.tran;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ram.kettle.database.DataSourceApplication;
import com.ram.kettle.database.DatabaseMeta;
import com.ram.kettle.log.ConstLog;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMeta;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepErrorMeta;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.KettleEnvironment;
import com.ram.kettle.xml.KettleVFS;
import com.ram.kettle.xml.XMLHandler;

public class TransMeta {
	private static final String XML_TAG_INFO = "info";
	private static final String XML_TAG_ORDER = "order";
	private static final String XML_TAG_STEP_ERROR_HANDLING = "step_error_handling";
	public static final String XML_TAG = "transformation";
	private List<StepMeta> steps;
	private List<TransHopMeta> hops;
	private String name;

	private String folder = "";

	private static final String DatabaseMeta_XML_TAG = "connection";

	public TransMeta(String fname) throws KettleException {
		ConstLog.message("TransMeta:" + fname);
		Document doc = null;
		try {
			FileObject fObject = KettleVFS.getFileObject(fname);
			folder = fObject.getParent().getURL().getPath();
			doc = XMLHandler.loadXMLFile(fObject);
		} catch (Exception e) {
			throw new KettleException(
					"TransMeta.Exception.ErrorOpeningOrValidatingTheXMLFile", e);
		}

		if (doc != null) {

			Node transnode = XMLHandler.getSubNode(doc, XML_TAG); //$NON-NLS-1$

			if (transnode == null) {
				throw new KettleException(
						"TransMeta.Exception.NotValidTransformationXML");
			}

			loadXML(transnode);

		} else {
			throw new KettleException(
					"TransMeta.Exception.ErrorOpeningOrValidatingTheXMLFile"); //$NON-NLS-1$
		}
	}

	public String getFolder() {
		return folder;
	}

	public void clear() {
		hops = new ArrayList<TransHopMeta>();
		steps = new ArrayList<StepMeta>();
	}

	public StepMeta findStep(String name) {
		return findStep(name, null);
	}

	public int indexOfStep(StepMeta stepMeta) {
		return steps.indexOf(stepMeta);
	}

	public int nrSteps() {
		return steps.size();
	}

	public StepMeta getStep(int i) {
		return steps.get(i);
	}

	public StepMeta findStep(String name, StepMeta exclude) {
		if (name == null)
			return null;

		int excl = -1;
		if (exclude != null)
			excl = indexOfStep(exclude);

		for (int i = 0; i < nrSteps(); i++) {
			StepMeta stepMeta = getStep(i);
			if (i != excl && stepMeta.getName().equalsIgnoreCase(name)) {
				return stepMeta;
			}
		}
		return null;
	}

	public void addStep(StepMeta stepMeta) {
		steps.add(stepMeta);
		stepMeta.setParentTransMeta(this);
	}

	public StepMeta findPrevStep(StepMeta stepMeta, int nr) {
		return this.findPrevStep(stepMeta, nr, true);
	}

	public StepMeta findPrevStep(StepMeta stepMeta, int nr, boolean info) {
		int count = 0;
		int i;

		for (i = 0; i < nrTransHops(); i++) {
			TransHopMeta hi = getTransHop(i);
			if (hi.getToStep() != null && hi.isEnabled()
					&& hi.getToStep().equals(stepMeta)) {
				if (info) {
					if (count == nr) {
						return hi.getFromStep();
					}
					count++;
				}
			}
		}
		return null;
	}

	public RowMetaInterface getPrevStepFields(StepMeta stepMeta)
			throws KettleException {

		RowMetaInterface row = new RowMeta();

		if (stepMeta == null) {
			return null;
		}

		StepMeta prevStepMeta = findPrevStep(stepMeta, 0);

		RowMetaInterface add = getStepFields(prevStepMeta, stepMeta);

		row.addRowMeta(add); // recursive!

		return row;
	}

	public RowMetaInterface getStepFields(StepMeta stepMeta, StepMeta targetStep)
			throws KettleException {
		RowMetaInterface row = new RowMeta();

		if (stepMeta == null)
			return row;

		RowMetaInterface rowMeta = null;

		if (targetStep != null) {

			row = getPrevStepFields(stepMeta);

			StepErrorMeta stepErrorMeta = stepMeta.getStepErrorMeta();
			row.addRowMeta(stepErrorMeta.getErrorFields());

			return row;
		}
		StepMeta prevStepMeta = findPrevStep(stepMeta, 0);

		RowMetaInterface add = getStepFields(prevStepMeta, stepMeta);
		if (add == null)
			add = new RowMeta();

		row.addRowMeta(add);

		rowMeta = getThisStepFields(stepMeta, targetStep, row);

		return rowMeta;
	}

	public RowMetaInterface getThisStepFields(StepMeta stepMeta,
			StepMeta nextStep, RowMetaInterface row) throws KettleException {
		String name = stepMeta.getName();
		stepMeta.getFields(row, name);
		return row;
	}

	public RowMetaInterface getStepFields(StepMeta stepMeta)
			throws KettleException {
		RowMetaInterface row = new RowMeta();
		StepMeta prevStepMeta = findPrevStep(stepMeta, 0, true);

		RowMetaInterface add = getStepFields(prevStepMeta, stepMeta);
		if (add == null)
			add = new RowMeta();

		row.addRowMeta(add);

		return row;
	}

	private static DataSourceApplication instance = DataSourceApplication
			.getInstanceSingle();

	@SuppressWarnings("unused")
	public void loadXML(Node transnode) throws KettleException {
		try {
			clear();
			int n = 0;
			ConstLog.message("只接受share.xml中的数据库配置");
			if (true) {
				n = XMLHandler.countNodes(transnode, DatabaseMeta_XML_TAG); //$NON-NLS-1$
				for (int i = 0; i < n; i++) {
					Node stepnode = XMLHandler.getSubNodeByNr(transnode,
							DatabaseMeta_XML_TAG, i);
					try {
						String dbName = XMLHandler
								.getTagValue(stepnode, "name");
						// 至入最新入得 这样通用配置可以独立
						if (instance.getMeta(dbName) == null) {
							DatabaseMeta dataBaseMeta = new DatabaseMeta(
									stepnode);
							instance.putMeta(dataBaseMeta.getName(),
									dataBaseMeta);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// Handle Steps
			int s = XMLHandler.countNodes(transnode, StepMeta.XML_TAG); //$NON-NLS-1$

			for (int i = 0; i < s; i++) {
				Node stepnode = XMLHandler.getSubNodeByNr(transnode,
						StepMeta.XML_TAG, i);

				String stepid = XMLHandler.getTagValue(stepnode, "type");
				// 根据stepid 生成meta 执行
				String className = KettleEnvironment.getClassName(stepid);
				Class clazz = Class.forName(className);
				Object classObj = clazz.newInstance();
				StepMeta stepMeta = (StepMeta) classObj;
				stepMeta.setParentTransMeta(this);
				stepMeta.setNode(stepnode);
				StepMeta check = findStep(stepMeta.getName());
				if (check != null) {

				} else {
					addStep(stepMeta); // simply add it.
				}
			}

			// Read the error handling code of the steps...
			//
			Node errorHandlingNode = XMLHandler.getSubNode(transnode,
					XML_TAG_STEP_ERROR_HANDLING);
			int nrErrorHandlers = XMLHandler.countNodes(errorHandlingNode,
					StepErrorMeta.XML_TAG);
			for (int i = 0; i < nrErrorHandlers; i++) {
				Node stepErrorMetaNode = XMLHandler.getSubNodeByNr(
						errorHandlingNode, StepErrorMeta.XML_TAG, i);
				StepErrorMeta stepErrorMeta = new StepErrorMeta(
						stepErrorMetaNode, steps);
				stepErrorMeta.getSourceStep().setStepErrorMeta(stepErrorMeta);
			}

			Node ordernode = XMLHandler.getSubNode(transnode, XML_TAG_ORDER); //$NON-NLS-1$
			n = XMLHandler.countNodes(ordernode, TransHopMeta.XML_TAG); //$NON-NLS-1$

			for (int i = 0; i < n; i++) {

				Node hopnode = XMLHandler.getSubNodeByNr(ordernode,
						TransHopMeta.XML_TAG, i); //$NON-NLS-1$

				TransHopMeta hopinf = new TransHopMeta(hopnode, steps);
				addTransHop(hopinf);
			}

			//
			// get transformation info:
			//
			Node infonode = XMLHandler.getSubNode(transnode, XML_TAG_INFO); //$NON-NLS-1$

			// Name
			//
			setName(XMLHandler.getTagValue(infonode, "name")); //$NON-NLS-1$

			// description
			//
			// description = XMLHandler.getTagValue(infonode, "description");

			// extended description
			//
			// extended_description = XMLHandler.getTagValue(infonode,
			// "extended_description");

			// trans version
			//
			// trans_version = XMLHandler.getTagValue(infonode,
			// "trans_version");

			// trans status
			//
			// trans_status = Const.toInt(
			// XMLHandler.getTagValue(infonode, "trans_status"), -1);

			// String transTypeCode = XMLHandler.getTagValue(infonode,
			// "trans_type");
			// transformationType = TransformationType
			// .getTransformationTypeByCode(transTypeCode);

			sortSteps();
		} catch (KettleException e) {
			e.printStackTrace();
			throw new KettleException(e);
		} catch (Exception e) {
			throw new KettleException(e);
		} finally {

		}
	}

	public void sortSteps() {
		try {
			Collections.sort(steps);
		} catch (Exception e) {
		}
	}

	public void setName(String newName) {

		this.name = newName;
	}

	public void addTransHop(TransHopMeta hi) {
		hops.add(hi);
	}

	public List<StepMeta> getSteps() {
		return steps;
	}

	public void setSteps(List<StepMeta> steps) {
		this.steps = steps;
	}

	public List<TransHopMeta> getHops() {
		return hops;
	}

	public void setHops(List<TransHopMeta> hops) {
		this.hops = hops;
	}

	public String getName() {
		return name;
	}

	public int nrTransHops() {
		return hops.size();
	}

	public TransHopMeta getTransHop(int i) {
		return hops.get(i);
	}

	public StepMeta[] getPrevSteps(StepMeta stepMeta) {
		List<StepMeta> prevSteps = new ArrayList<StepMeta>();
		for (int i = 0; i < nrTransHops(); i++) // Look at all the hops;
		{
			TransHopMeta hopMeta = getTransHop(i);
			if (hopMeta.isEnabled() && hopMeta.getToStep().equals(stepMeta)) {
				prevSteps.add(hopMeta.getFromStep());
			}
		}

		return prevSteps.toArray(new StepMeta[prevSteps.size()]);
	}

	/**
	 * Retrieve an array of succeeding step names for a certain originating step
	 * name.
	 * 
	 * @param stepname
	 *            The originating step name
	 * @return An array of succeeding step names
	 */
	public String[] getPrevStepNames(String stepname) {
		return getPrevStepNames(findStep(stepname));
	}

	/**
	 * Retrieve an array of preceding steps for a certain destination step.
	 * 
	 * @param stepMeta
	 *            The destination step
	 * @return an array of preceding step names.
	 */
	public String[] getPrevStepNames(StepMeta stepMeta) {
		StepMeta prevStepMetas[] = getPrevSteps(stepMeta);
		String retval[] = new String[prevStepMetas.length];
		for (int x = 0; x < prevStepMetas.length; x++)
			retval[x] = prevStepMetas[x].getName();

		return retval;
	}

	/**
	 * Retrieve an array of succeeding steps for a certain originating step.
	 * 
	 * @param stepMeta
	 *            The originating step
	 * @return an array of succeeding steps.
	 * @deprecated use findNextSteps instead
	 */
	public StepMeta[] getNextSteps(StepMeta stepMeta) {
		List<StepMeta> nextSteps = new ArrayList<StepMeta>();
		for (int i = 0; i < nrTransHops(); i++) // Look at all the hops;
		{
			TransHopMeta hi = getTransHop(i);
			if (hi.isEnabled() && hi.getFromStep().equals(stepMeta)) {
				nextSteps.add(hi.getToStep());
			}
		}

		return nextSteps.toArray(new StepMeta[nextSteps.size()]);
	}

	/**
	 * Retrieve a list of succeeding steps for a certain originating step.
	 * 
	 * @param stepMeta
	 *            The originating step
	 * @return an array of succeeding steps.
	 */
	public List<StepMeta> findNextSteps(StepMeta stepMeta) {
		List<StepMeta> nextSteps = new ArrayList<StepMeta>();
		for (int i = 0; i < nrTransHops(); i++) // Look at all the hops;
		{
			TransHopMeta hi = getTransHop(i);
			if (hi.isEnabled() && hi.getFromStep().equals(stepMeta)) {
				nextSteps.add(hi.getToStep());
			}
		}

		return nextSteps;
	}

	/**
	 * Retrieve an array of succeeding step names for a certain originating
	 * step.
	 * 
	 * @param stepMeta
	 *            The originating step
	 * @return an array of succeeding step names.
	 */
	public String[] getNextStepNames(StepMeta stepMeta) {
		StepMeta nextStepMeta[] = getNextSteps(stepMeta);
		String retval[] = new String[nextStepMeta.length];
		for (int x = 0; x < nextStepMeta.length; x++)
			retval[x] = nextStepMeta[x].getName();

		return retval;
	}

	/**
	 * all true 表示所有 false表示只取有效
	 * */
	public List<StepMeta> getTransHopSteps(boolean all) {
		List<StepMeta> st = new ArrayList<StepMeta>();
		int idx;

		for (int x = 0; x < nrTransHops(); x++) {
			TransHopMeta hi = getTransHop(x);
			if (hi.isEnabled() || all) {
				idx = st.indexOf(hi.getFromStep()); // FROM
				if (idx < 0)
					st.add(hi.getFromStep());

				idx = st.indexOf(hi.getToStep()); // TO
				if (idx < 0)
					st.add(hi.getToStep());
			}
		}

		for (int x = 0; x < nrSteps(); x++) {
			StepMeta stepMeta = getStep(x);
			st.add(stepMeta);

		}

		return st;
	}

}
