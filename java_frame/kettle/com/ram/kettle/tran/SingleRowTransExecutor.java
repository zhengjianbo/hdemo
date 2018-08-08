package com.ram.kettle.tran;

import java.util.List;

import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.util.Const;

public class SingleRowTransExecutor implements Cloneable {

	public static final String INPUT = "INPUT";
	public static final String OUTPUT = "OUTPUT";

	private final int MAX_STEPS = 1000;
	// 单线程 并行执行， WEB 状态下使用 高并发
	private Trans trans = null;
	private List<StepMeta> steps;

	private StepMeta startCombi, endCombi;

	public Object Clone() throws Exception {
		SingleRowTransExecutor o = null;
		try {
			o = (SingleRowTransExecutor) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Exception(e);
		}

		return o;
	}

	public SingleRowTransExecutor(final Trans trans, final String inputStep,
			final String outPutStep) throws Exception {
		this.trans = trans;
		steps = this.trans.getSteps();

		for (StepMeta combi : steps) {
			if (combi.getName().equalsIgnoreCase(inputStep)) {
				startCombi = combi;
			}
			// else if (combi.getName().equalsIgnoreCase(outPutStep)) {
			// endCombi = combi;
			// }
		}
	}

	public boolean init() throws KettleException {

		for (StepMeta combi : steps) {
			boolean ok = combi.init();
			if (!ok) {
				return false;
			}
		}
		return true;

	}

	// 这里可有优化，把 下一步骤 放到step中，这样就不需要查找
	public StepMeta findStepMeta(String stepname) {
	 
		if (Const.isEmpty(steps))
			return null;
		int stepSize = steps.size();
		for (int i = 0; i < stepSize; i++) {
			StepMeta sid = steps.get(i);
			if (sid.getName().equalsIgnoreCase(stepname)) {
				return sid;
			}
		}
		return null;
	}

	public ProcessReturn oneIteration(RowMetaInterface rowMeta, Object[] row)
			throws KettleException {
		// 获取开始插件
		if (startCombi == null) {
			throw new KettleException(
					"oneIteration INIT ERROR: NO FOUND STARTCOMBI");
		}
		if (Const.isEmpty(startCombi.getName())) {
			throw new KettleException(
					"oneIteration INIT ERROR: NO FOUND STARTCOMBI");
		}

		String stepName = startCombi.getName();

		ProcessReturn pReturn = startCombi.processSingleRow(rowMeta, row);
		pReturn.setThisStream(startCombi.getName());
		int i = 0;
		while (i < MAX_STEPS) {
			if (pReturn == null) {
				throw new KettleException("STEP NO DATA RETURN(" + stepName
						+ ")");
			}
			String nextStream = pReturn.getNextStream();
			if (Const.isEmpty(nextStream)) { 
				return pReturn;
//				throw new KettleException("Unable to find Next Step");
			}
			// 循环执行 限制1000个插件
			StepMeta combi = findStepMeta(nextStream);
			if (combi == null) {
				throw new KettleException("Unable to find Step with name("
						+ nextStream + ")");
			}
			stepName = combi.getName();
			pReturn = combi.processSingleRow(pReturn.getRowMeta(),
					pReturn.getRow());
			pReturn.setThisStream(stepName);
			// if (combi.getName().equalsIgnoreCase(endCombi.getName())) {
			// pReturn.setNextStream(null);
			// return pReturn;
			// }
			if (Const.isEmpty(combi.getNextStepName())) { // 加上一条不一定需要最后的是OUTPUT
				pReturn.setNextStream(null);
				return pReturn;
			}
			i++;
		}

		throw new KettleException("No Found OutPut Step");
	}

	public void dispose() throws KettleException {
		for (StepMeta combi : steps) {
			combi.dispose();
		}

	}
}