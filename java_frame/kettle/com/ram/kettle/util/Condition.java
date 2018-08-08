package com.ram.kettle.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.ram.kettle.log.KettleException;
import com.ram.kettle.row.RowMetaInterface;
import com.ram.kettle.value.ValueMetaAndData;
import com.ram.kettle.value.ValueMetaInterface;
import com.ram.kettle.xml.XMLHandler;
import com.sun.corba.se.spi.ior.ObjectId;

public class Condition implements Cloneable {
	public static final String XML_TAG = "condition";

	public static final String[] operators = new String[] { "-", "OR", "AND",
			"NOT", "OR NOT", "AND NOT", "XOR" };
	public static final int OPERATOR_NONE = 0;
	public static final int OPERATOR_OR = 1;
	public static final int OPERATOR_AND = 2;
	public static final int OPERATOR_NOT = 3;
	public static final int OPERATOR_OR_NOT = 4;
	public static final int OPERATOR_AND_NOT = 5;
	public static final int OPERATOR_XOR = 6;

	public static final String[] functions = new String[] { "=", "<>", "<",
			"<=", ">", ">=", "REGEXP", "IS NULL", "IS NOT NULL", "IN LIST",
			"CONTAINS", "STARTS WITH", "ENDS WITH" };

	public static final int FUNC_EQUAL = 0;
	public static final int FUNC_NOT_EQUAL = 1;
	public static final int FUNC_SMALLER = 2;
	public static final int FUNC_SMALLER_EQUAL = 3;
	public static final int FUNC_LARGER = 4;
	public static final int FUNC_LARGER_EQUAL = 5;
	public static final int FUNC_REGEXP = 6;
	public static final int FUNC_NULL = 7;
	public static final int FUNC_NOT_NULL = 8;
	public static final int FUNC_IN_LIST = 9;
	public static final int FUNC_CONTAINS = 10;
	public static final int FUNC_STARTS_WITH = 11;
	public static final int FUNC_ENDS_WITH = 12;

	//
	// These parameters allow for:
	// value = othervalue
	// value = 'A'
	// NOT value = othervalue
	//

	private ObjectId id;

	private boolean negate;
	private int operator;
	private String left_valuename;
	private int function;
	private String right_valuename;
	private ValueMetaAndData right_exact;
	private ObjectId id_right_exact;

	private int left_fieldnr;
	private int right_fieldnr;

	private ArrayList<Condition> list;

	private String right_string;

	/**
	 * Temporary variable, no need to persist this one. Contains the sorted
	 * array of strings in an IN LIST condition
	 */
	private String[] inList;

	public Condition() {
		list = new ArrayList<Condition>();
		this.operator = OPERATOR_NONE;
		this.negate = false;

		left_fieldnr = -2;
		right_fieldnr = -2;

		id = null;
	}

	public Condition(String valuename, int function, String valuename2,
			ValueMetaAndData exact) {
		this();
		this.left_valuename = valuename;
		this.function = function;
		this.right_valuename = valuename2;
		this.right_exact = exact;

		clearFieldPositions();
	}

	public Condition(int operator, String valuename, int function,
			String valuename2, ValueMetaAndData exact) {
		this();
		this.operator = operator;
		this.left_valuename = valuename;
		this.function = function;
		this.right_valuename = valuename2;
		this.right_exact = exact;

		clearFieldPositions();
	}

	public Condition(boolean negate, String valuename, int function,
			String valuename2, ValueMetaAndData exact) {
		this(valuename, function, valuename2, exact);
		this.negate = negate;
	}

	/**
	 * Returns the database ID of this Condition if a repository was used
	 * before.
	 * 
	 * @return the ID of the db connection.
	 */
	public ObjectId getObjectId() {
		return id;
	}

	/**
	 * Set the database ID for this Condition in the repository.
	 * 
	 * @param id
	 *            The ID to set on this condition.
	 * 
	 */
	public void setObjectId(ObjectId id) {
		this.id = id;
	}

	public Object clone() {
		Condition retval = null;

		retval = new Condition();
		retval.negate = negate;
		retval.operator = operator;

		if (isComposite()) {
			for (int i = 0; i < nrConditions(); i++) {
				Condition c = getCondition(i);
				Condition cCopy = (Condition) c.clone();
				retval.addCondition(cCopy);
			}
		} else {
			retval.negate = negate;
			retval.left_valuename = left_valuename;
			retval.operator = operator;
			retval.right_valuename = right_valuename;
			retval.function = function;
			if (right_exact != null) {
				retval.right_exact = (ValueMetaAndData) right_exact.clone();
			} else {
				retval.right_exact = null;
			}
		}

		return retval;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public int getOperator() {
		return operator;
	}

	public String getOperatorDesc() {
		return Const.rightPad(operators[operator], 7);
	}

	public static final int getOperator(String description) {
		if (description == null)
			return OPERATOR_NONE;

		for (int i = 1; i < operators.length; i++) {
			if (operators[i].equalsIgnoreCase(Const.trim(description)))
				return i;
		}
		return OPERATOR_NONE;
	}

	public static final String[] getOperators() {
		String retval[] = new String[operators.length - 1];
		for (int i = 1; i < operators.length; i++) {
			retval[i - 1] = operators[i];
		}
		return retval;
	}

	public static final String[] getRealOperators() {
		return new String[] { "OR", "AND", "OR NOT", "AND NOT", "XOR" };
	}

	public void setLeftValuename(String left_valuename) {
		this.left_valuename = left_valuename;
	}

	public String getLeftValuename() {
		return left_valuename;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	public String getFunctionDesc() {
		return functions[function];
	}

	public static final int getFunction(String description) {
		for (int i = 1; i < functions.length; i++) {
			if (functions[i].equalsIgnoreCase(Const.trim(description)))
				return i;
		}
		return FUNC_EQUAL;
	}

	public void setRightValuename(String right_valuename) {
		this.right_valuename = right_valuename;
	}

	public String getRightValuename() {
		return right_valuename;
	}

	public void setRightExact(ValueMetaAndData right_exact) {
		this.right_exact = right_exact;
	}

	public ValueMetaAndData getRightExact() {
		return right_exact;
	}

	public String getRightExactString() {
		if (right_exact == null)
			return null;
		return right_exact.toString();
	}

	/**
	 * Get the id of the RightExact Value in the repository
	 * 
	 * @return The id of the RightExact Value in the repository
	 */
	public ObjectId getRightExactID() {
		return id_right_exact;
	}

	/**
	 * Set the database ID for the RightExact Value in the repository.
	 * 
	 * @param id_right_exact
	 *            The ID to set on this Value.
	 * 
	 */
	public void setRightExactID(ObjectId id_right_exact) {
		this.id_right_exact = id_right_exact;
	}

	public boolean isAtomic() {
		return list.size() == 0;
	}

	public boolean isComposite() {
		return list.size() != 0;
	}

	public boolean isNegated() {
		return negate;
	}

	public void setNegated(boolean negate) {
		this.negate = negate;
	}

	public void negate() {
		setNegated(!isNegated());
	}

	/**
	 * A condition is empty when the condition is atomic and no left field is
	 * specified.
	 */
	public boolean isEmpty() {
		return (isAtomic() && left_valuename == null);
	}

	/**
	 * We cache the position of a value in a row. If ever we want to change the
	 * rowtype, we need to clear these cached field positions...
	 */
	public void clearFieldPositions() {
		left_fieldnr = -2;
		right_fieldnr = -2;
	}

	/**
	 * 蒋当前的Condition 组合输出SQL条件
	 * 
	 * @return
	 */
	public String getEvaluateSql(RowMetaInterface rowMeta) {
		try {
			if (isAtomic()) {
				// if (left_valuename!=null && left_valuename.length()>0 &&
				// left_fieldnr<-1) left_fieldnr =
				// rowMeta.indexOfValue(left_valuename);
				// if (right_valuename!=null && right_valuename.length()>0 &&
				// right_fieldnr<-1) right_fieldnr =
				// rowMeta.indexOfValue(right_valuename);

				Object field2 = right_exact != null ? right_exact
						.getValueData() : null;

				String filterSql = "";
				switch (function) {
				case FUNC_EQUAL:
					if (field2 == null) {
						filterSql = " " + left_valuename + " is null ";
					} else {
						filterSql = " " + left_valuename + "='" + field2 + "' ";
					}
					break;
				case FUNC_NOT_EQUAL:
					if (field2 == null) {
						filterSql = " " + left_valuename + " is not null ";
					} else {
						filterSql = " " + left_valuename + "<>'" + field2
								+ "' ";
					}
					break;
				case FUNC_SMALLER:
					filterSql = " " + left_valuename + "<" + field2 + "  ";
					break;
				case FUNC_SMALLER_EQUAL:
					filterSql = " " + left_valuename + "<=" + field2 + "  ";
					break;
				case FUNC_LARGER:
					filterSql = " " + left_valuename + ">" + field2 + "  ";
					break;
				case FUNC_LARGER_EQUAL:
					filterSql = " " + left_valuename + ">=" + field2 + "  ";
					break;

				case FUNC_NULL:
					filterSql = " " + left_valuename + " is null ";
					break;
				case FUNC_NOT_NULL:
					filterSql = " " + left_valuename + " is not null ";
					break;

				default:
					break;
				}

				// Only NOT makes sense, the rest doesn't, so ignore!!!!
				// Optionally negate
				//
				if (isNegated()) {
					filterSql = " not ( " + filterSql + " ) ";
				}
				return filterSql;
			} else {
				String filterSql = " ( ";
				for (int i = 0; i < list.size(); i++) {
					Condition cb = list.get(i);
					switch (cb.getOperator()) {

					case Condition.OPERATOR_NONE:
						filterSql += cb.getEvaluateSql(rowMeta);
						break;
					case Condition.OPERATOR_OR:
						filterSql += (i == 0 ? "" : " or ") + " ( "
								+ cb.getEvaluateSql(rowMeta) + " ) ";
						break;
					case Condition.OPERATOR_AND:
						filterSql += (i == 0 ? "" : " and ") + " ( "
								+ cb.getEvaluateSql(rowMeta) + " ) ";
						break;
					case Condition.OPERATOR_OR_NOT:
						filterSql += (i == 0 ? "" : " or not ") + " ( "
								+ cb.getEvaluateSql(rowMeta) + " ) ";
						break;
					case Condition.OPERATOR_AND_NOT:
						filterSql += (i == 0 ? "" : " and not ") + " ( "
								+ cb.getEvaluateSql(rowMeta) + " ) ";
						break;
					// case Condition.OPERATOR_XOR:
					// filterSql += ( i==0?"":" or ")+ " ( " +
					// cb.getEvaluateSql(rowMeta)
					// + " ) ";
					// break;
					default:
						break;
					}
				}
				filterSql += " ) ";
				if (isNegated()) {
					filterSql = " not ( " + filterSql + " ) ";
				}
				return filterSql;
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected error evaluation condition [" + toString()
							+ "]", e);
		}
	}

	//
	// Evaluate the condition...
	//
	public boolean evaluate(RowMetaInterface rowMeta, Object[] r) {
		// Start of evaluate
		boolean retval = false;

		// If we have 0 items in the list, evaluate the current condition
		// Otherwise, evaluate all sub-conditions
		//
		try {
			if (isAtomic()) {
				// Get fieldnrs left value
				//
				// Check out the fieldnrs if we don't have them...
				if (left_valuename != null && left_valuename.length() > 0
						&& left_fieldnr < -1)
					left_fieldnr = rowMeta.indexOfValue(left_valuename);

				// Get fieldnrs right value
				//
				if (right_valuename != null && right_valuename.length() > 0
						&& right_fieldnr < -1)
					right_fieldnr = rowMeta.indexOfValue(right_valuename);

				// Get fieldnrs left field
				ValueMetaInterface fieldMeta = null;
				Object field = null;
				if (left_fieldnr >= 0) {
					fieldMeta = rowMeta.getValueMeta(left_fieldnr);
					field = r[left_fieldnr];
					// JIRA PDI-38
					// if (field==null)
					// {
					// throw new
					// KettleException("Unable to find field ["+left_valuename+"] in the input row!");
					// }
				} else
					return false; // no fields to evaluate

				// Get fieldnrs right exact
				ValueMetaInterface fieldMeta2 = right_exact != null ? right_exact
						.getValueMeta() : null;
				Object field2 = right_exact != null ? right_exact
						.getValueData() : null;
				if (field2 == null && right_fieldnr >= 0) {
					fieldMeta2 = rowMeta.getValueMeta(right_fieldnr);
					field2 = r[right_fieldnr];
					// JIRA PDI-38
					// if (field2==null)
					// {
					// throw new
					// KettleException("Unable to find field ["+right_valuename+"] in the input row!");
					// }
				}

				switch (function) {
				case FUNC_EQUAL:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) == 0);
					break;
				case FUNC_NOT_EQUAL:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) != 0);
					break;
				case FUNC_SMALLER:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) < 0);
					break;
				case FUNC_SMALLER_EQUAL:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) <= 0);
					break;
				case FUNC_LARGER:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) > 0);
					break;
				case FUNC_LARGER_EQUAL:
					retval = (fieldMeta.compare(field, fieldMeta2, field2) >= 0);
					break;
				case FUNC_REGEXP:
					if (fieldMeta.isNull(field) || field2 == null) {
						retval = false;
					} else {
						retval = Pattern.matches(
								fieldMeta2.getCompatibleString(field2),
								fieldMeta.getCompatibleString(field));
					}
					break;
				case FUNC_NULL:
					retval = (fieldMeta.isNull(field));
					break;
				case FUNC_NOT_NULL:
					retval = (!fieldMeta.isNull(field));
					break;
				case FUNC_IN_LIST:
					if (inList == null) {
						inList = Const.splitString(
								fieldMeta2.getString(field2), ';');
						Arrays.sort(inList);
					}
					String searchString = fieldMeta.getCompatibleString(field);
					int inIndex = -1;
					if (searchString != null) {
						inIndex = Arrays.binarySearch(inList, searchString);
					}
					retval = Boolean.valueOf(inIndex >= 0);
					break;
				case FUNC_CONTAINS:
					retval = fieldMeta.getCompatibleString(field) != null ? fieldMeta
							.getCompatibleString(field).indexOf(
									fieldMeta2.getCompatibleString(field2)) >= 0
							: false;
					break;
				case FUNC_STARTS_WITH:
					retval = fieldMeta.getCompatibleString(field) != null ? fieldMeta
							.getCompatibleString(field).startsWith(
									fieldMeta2.getCompatibleString(field2))
							: false;
					break;
				case FUNC_ENDS_WITH:
					String string = fieldMeta.getCompatibleString(field);
					if (!Const.isEmpty(string)) {
						if (right_string == null && field2 != null)
							right_string = fieldMeta2
									.getCompatibleString(field2);
						if (right_string != null) {
							retval = string.endsWith(fieldMeta2
									.getCompatibleString(field2));
						} else {
							retval = false;
						}
					} else {
						retval = false;
					}
					break;
				default:
					break;
				}

				// Only NOT makes sense, the rest doesn't, so ignore!!!!
				// Optionally negate
				//
				if (isNegated())
					retval = !retval;
			} else {
				// Composite : get first
				Condition cb0 = list.get(0);
				retval = cb0.evaluate(rowMeta, r);

				// Loop over the conditions listed below.
				//
				for (int i = 1; i < list.size(); i++) {
					// Composite : #i
					// Get right hand condition
					Condition cb = list.get(i);

					// Evaluate the right hand side of the condition
					// cb.evaluate() within the switch statement
					// because the condition may be short-circuited due to the
					// left hand side (retval)
					switch (cb.getOperator()) {
					case Condition.OPERATOR_OR:
						retval = retval || cb.evaluate(rowMeta, r);
						break;
					case Condition.OPERATOR_AND:
						retval = retval && cb.evaluate(rowMeta, r);
						break;
					case Condition.OPERATOR_OR_NOT:
						retval = retval || (!cb.evaluate(rowMeta, r));
						break;
					case Condition.OPERATOR_AND_NOT:
						retval = retval && (!cb.evaluate(rowMeta, r));
						break;
					case Condition.OPERATOR_XOR:
						retval = retval ^ cb.evaluate(rowMeta, r);
						break;
					default:
						break;
					}
				}

				// Composite: optionally negate
				if (isNegated())
					retval = !retval;
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected error evaluation condition [" + toString()
							+ "]", e);
		}

		return retval;
	}

	public void addCondition(Condition cb) {
		if (isAtomic() && getLeftValuename() != null) {
			/*
			 * Copy current atomic setup...
			 */
			Condition current = new Condition(getLeftValuename(),
					getFunction(), getRightValuename(), getRightExact());
			current.setNegated(isNegated());
			setNegated(false);
			list.add(current);
		} else
		// Set default operator if not on first position...
		if (isComposite() && list.size() > 0
				&& cb.getOperator() == OPERATOR_NONE) {
			cb.setOperator(OPERATOR_AND);
		}
		list.add(cb);
	}

	public void addCondition(int idx, Condition cb) {
		if (isAtomic() && getLeftValuename() != null) {
			/*
			 * Copy current atomic setup...
			 */
			Condition current = new Condition(getLeftValuename(),
					getFunction(), getRightValuename(), getRightExact());
			current.setNegated(isNegated());
			setNegated(false);
			list.add(current);
		} else
		// Set default operator if not on first position...
		if (isComposite() && idx > 0 && cb.getOperator() == OPERATOR_NONE) {
			cb.setOperator(OPERATOR_AND);
		}
		list.add(idx, cb);
	}

	public void removeCondition(int nr) {
		if (isComposite()) {
			Condition c = list.get(nr);
			list.remove(nr);

			// Nothing left or only one condition left: move it to the parent:
			// make it atomic.

			boolean moveUp = isAtomic() || nrConditions() == 1;
			if (nrConditions() == 1)
				c = getCondition(0);

			if (moveUp) {
				setLeftValuename(c.getLeftValuename());
				setFunction(c.getFunction());
				setRightValuename(c.getRightValuename());
				setRightExact(c.getRightExact());
				setNegated(c.isNegated());
			}
		}
	}

	public int nrConditions() {
		return list.size();
	}

	public Condition getCondition(int i) {
		return list.get(i);
	}

	public void setCondition(int i, Condition subCondition) {
		list.set(i, subCondition);
	}

	public String toString() {
		return toString(0, true, true);
	}

	public String toString(int level, boolean show_negate, boolean show_operator) {
		String retval = "";

		if (isAtomic()) {
			// retval+="<ATOMIC "+level+", "+show_negate+", "+show_operator+">";

			for (int i = 0; i < level; i++)
				retval += "  ";

			if (show_operator && getOperator() != OPERATOR_NONE) {
				retval += getOperatorDesc() + " ";
			} else {
				retval += "        ";
			}

			// Atomic is negated?
			if (isNegated() && (show_negate || level > 0)) {
				retval += "NOT ( ";
			} else {
				retval += "      ";
			}
			retval += left_valuename + " " + getFunctionDesc();
			if (function != FUNC_NULL && function != FUNC_NOT_NULL) {
				if (right_valuename != null) {
					retval += " " + right_valuename;
				} else {
					retval += " ["
							+ (getRightExactString() == null ? ""
									: getRightExactString()) + "]";
				}
			}

			if (isNegated() && (show_negate || level > 0))
				retval += " )";

			retval += Const.CR;
		} else {
			// retval+="<COMP "+level+", "+show_negate+", "+show_operator+">";

			// Group is negated?
			if (isNegated() && (show_negate || level > 0)) {
				for (int i = 0; i < level; i++)
					retval += "  ";
				retval += "NOT" + Const.CR;
			}
			// Group is preceded by an operator:
			if (getOperator() != OPERATOR_NONE && (show_operator || level > 0)) {
				for (int i = 0; i < level; i++)
					retval += "  ";
				retval += getOperatorDesc() + Const.CR;
			}
			for (int i = 0; i < level; i++)
				retval += "  ";
			retval += "(" + Const.CR;
			for (int i = 0; i < list.size(); i++) {
				Condition cb = list.get(i);
				retval += cb.toString(level + 1, true, i > 0);
			}
			for (int i = 0; i < level; i++)
				retval += "  ";
			retval += ")" + Const.CR;
		}

		return retval;
	}
 

	/**
	 * Build a new condition using an XML Document Node
	 * 
	 * @param condnode
	 * @throws KettleException
	 */
	public Condition(Node condnode) throws KettleException {
		this();

		list = new ArrayList<Condition>();
		try {
			String str_negated = XMLHandler.getTagValue(condnode, "negated");
			setNegated("Y".equalsIgnoreCase(str_negated));

			String str_operator = XMLHandler.getTagValue(condnode, "operator");
			setOperator(getOperator(str_operator));

			Node conditions = XMLHandler.getSubNode(condnode, "conditions");
			int nrconditions = XMLHandler.countNodes(conditions, "condition");
			if (nrconditions == 0) // ATOMIC!
			{
				setLeftValuename(XMLHandler.getTagValue(condnode, "leftvalue"));
				setFunction(getFunction(XMLHandler.getTagValue(condnode,
						"function")));
				setRightValuename(XMLHandler
						.getTagValue(condnode, "rightvalue"));
				Node exactnode = XMLHandler.getSubNode(condnode,
						ValueMetaAndData.XML_TAG);
				if (exactnode != null) {
					ValueMetaAndData exact = new ValueMetaAndData(exactnode);
					setRightExact(exact);
				}
			} else {
				for (int i = 0; i < nrconditions; i++) {
					Node subcondnode = XMLHandler.getSubNodeByNr(conditions,
							"condition", i);
					Condition c = new Condition(subcondnode);
					addCondition(c);
				}
			}
		} catch (Exception e) {
			throw new KettleException("Unable to create condition using xml: "
					+ Const.CR + condnode, e);
		}
	}

	public String[] getUsedFields() {
		Hashtable<String, String> fields = new Hashtable<String, String>();
		getUsedFields(fields);

		String retval[] = new String[fields.size()];
		Enumeration<String> keys = fields.keys();
		int i = 0;
		while (keys.hasMoreElements()) {
			retval[i] = (String) keys.nextElement();
			i++;
		}

		return retval;
	}

	public void getUsedFields(Hashtable<String, String> fields) {
		if (isAtomic()) {
			if (getLeftValuename() != null)
				fields.put(getLeftValuename(), "-");
			if (getRightValuename() != null)
				fields.put(getRightValuename(), "-");
		} else {
			for (int i = 0; i < nrConditions(); i++) {
				Condition subc = getCondition(i);
				subc.getUsedFields(fields);
			}
		}
	}
}
