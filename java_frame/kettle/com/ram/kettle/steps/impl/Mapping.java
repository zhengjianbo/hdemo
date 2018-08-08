package com.ram.kettle.steps.impl;

import org.w3c.dom.Node;

import com.ram.kettle.database.KettleApplication;
import com.ram.kettle.element.ProcessReturn;
import com.ram.kettle.log.BaseMessages;
import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowDataUtil;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.step.StepInterface;
import com.ram.kettle.step.StepMeta;
import com.ram.kettle.tran.TransMeta;
import com.ram.kettle.util.Const;
import com.ram.kettle.util.KettleEnvironment;
import com.ram.kettle.xml.XMLHandler;

public class Mapping extends StepMeta implements StepInterface {
	private static Class<?> PKG = Mapping.class;
	private final String typeid = "Mapping";

	public static final String INPUT = KettleApplication.INPUT;
	public static final String OUTPUT = KettleApplication.OUTPUT;
	public static KettleApplication kApp = KettleApplication
			.getInstanceSingle();

	private String fileName;

	public String mappingName = null;

	public TransMeta mappingTransMeta;

	public Mapping() {
		super();
	}

	public Mapping(Node stepnode) throws KettleException {
		super(stepnode);
	}

	public void allocate(int nrFields) {

	}

	public Object clone() {
		Object retval = super.clone();
		return retval;
	}
	//加载内容 主要获取到转换名称
	public synchronized static final TransMeta loadMappingMeta(
			Mapping mappingMeta) throws KettleException {
		TransMeta mappingTransMeta = null;

		String realFilename = mappingMeta.getFileName();
		try {
			realFilename = realFilename.replace(
					"${Internal.Transformation.Filename.Directory}",
					mappingMeta.getFolder());
 		
			//如果load 全部  这里 只需要  mappingTransMeta = new TransMeta(realFilename);
			mappingTransMeta=KettleEnvironment.addTransMeta(realFilename, true);
			//如果没有存放内容则
			
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"MappingMeta.Exception.UnableToLoadMapping"), e);
		}

		return mappingTransMeta;
	}

	private String getFileName() {
		return fileName;
	}

	public void getFields(RowMetaInterface row, String origin)
			throws KettleException {

		StepMeta mappingOutputStep = mappingTransMeta
				.findStep(KettleApplication.OUTPUT);

		RowMetaInterface mappingOutputRowMeta = mappingTransMeta
				.getStepFields(mappingOutputStep);

		row.clear();
		row.addRowMeta(mappingOutputRowMeta);
	}

	public void setNode(Node stepnode) throws KettleException {
		super.setNode(stepnode);
		try {
//			String method = XMLHandler.getTagValue(stepnode,
//					"specification_method");

//			String transId = XMLHandler
//					.getTagValue(stepnode, "trans_object_id");

			fileName = XMLHandler.getTagValue(stepnode, "filename"); //$NON-NLS-1$ 

//			Node mappingsNode = XMLHandler.getSubNode(stepnode, "mappings"); //$NON-NLS-1$
//
//			String multiInput = XMLHandler.getTagValue(stepnode,
//					"allow_multiple_input");
//
//			String multiOutput = XMLHandler.getTagValue(stepnode,
//					"allow_multiple_output");

			mappingTransMeta = loadMappingMeta(this);

		} catch (Exception e) {
			throw new KettleException(
					BaseMessages
							.getString(PKG,
									"MappingMeta.Exception.ErrorLoadingTransformationStepFromXML"), e); //$NON-NLS-1$
		}
	}

	public String getTypeId() {
		return typeid;
	}

	public boolean init() {
		if (super.init()) {
			// ConstLog.message("==" + this.getTypeId() + "=初始化"
			// + this.getName());
		}
		return true;
	}

	@Override
	public ProcessReturn processSingleRow(RowMetaInterface rowMeta, Object[] r)
			throws KettleException {
		if (r == null) {
			throw new KettleException("DATA ERROR");
		}
		if (first) {
			synchronized (this) {
				if (first) {
					mappingName = mappingTransMeta.getName();
					first = false;
				}
			}
		}
		try {
			// 此处有改进空间：可以设置下input 字段对应inner里面的字段 配置inner里面的改进 提高效率
			ProcessReturn mappingReturn = (ProcessReturn) kApp.getReturnRow(
					mappingName, rowMeta, r); 
			if (Const.isEmpty(mappingReturn.getAbortStream())) {
				mappingReturn.setNextStream(null);
				return mappingReturn;
			}
			r = RowDataUtil.addRowData(r, rowMeta.size(),
					mappingReturn.getRow());
			mappingReturn.setRow(r);

			rowMeta.mergeRowMeta(mappingReturn.getRowMeta());
			mappingReturn.setRowMeta(rowMeta);

			mappingReturn.setNextStream(this.getNextStepName());

			return mappingReturn;
		} catch (Throwable t) {
			throw new KettleException(t);
		}
	}

	public String getTypeid() {
		return typeid;
	}
}
