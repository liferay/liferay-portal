/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class CriterionImpl implements Criterion {

	public CriterionImpl(CriterionType criterionType, Criterion... criterions) {
		_criterionType = criterionType;
		_criterions = ListUtil.fromArray(criterions);
	}

	public CriterionImpl(
		CriterionType criterionType, DynamicQuery dynamicQuery,
		String propertyName) {

		_criterionType = criterionType;
		_dynamicQuery = dynamicQuery;
		_propertyName = propertyName;
	}

	public CriterionImpl(
		CriterionType criterionType,
		Map<String, Criterion> propertyNameValues) {

		_criterionType = criterionType;
		_propertyNameValues = propertyNameValues;
	}

	public CriterionImpl(CriterionType criterionType, String propertyName) {
		_criterionType = criterionType;
		_propertyName = propertyName;
	}

	public CriterionImpl(
		CriterionType criterionType, String propertyName, int size) {

		_criterionType = criterionType;
		_propertyName = propertyName;
		_size = size;
	}

	public CriterionImpl(
		CriterionType criterionType, String propertyName, Object... values) {

		_criterionType = criterionType;
		_propertyName = propertyName;
		_values = values;
	}

	public CriterionImpl(
		CriterionType criterionType, String propertyName,
		String targetPropertyName) {

		_criterionType = criterionType;
		_propertyName = propertyName;
		_targetPropertyName = targetPropertyName;
	}

	public CriterionImpl(String sql, Type[] sqlTypes, Object[] values) {
		_sql = sql;
		_sqlTypes = sqlTypes;
		_values = values;

		_criterionType = CriterionType.SQL_RESTRICTION;
	}

	public CriterionType getCriterionType() {
		return _criterionType;
	}

	public List<Criterion> getCriterions() {
		return _criterions;
	}

	public DynamicQuery getDynamicQuery() {
		return _dynamicQuery;
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public Map<String, Criterion> getPropertyNameValues() {
		return _propertyNameValues;
	}

	public String getSQL() {
		return _sql;
	}

	public Type[] getSQLTypes() {
		return _sqlTypes;
	}

	public Integer getSize() {
		return _size;
	}

	public String getTargetPropertyName() {
		return _targetPropertyName;
	}

	public Object[] getValues() {
		return _values;
	}

	@Override
	public String toString() {
		if (_dynamicQuery != null) {
			if (_criterionType == CriterionType.EQ) {
				return StringBundler.concat(
					_propertyName, " = (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.EQ_ALL) {
				return StringBundler.concat(
					_propertyName, " =ALL (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GE) {
				return StringBundler.concat(
					_propertyName, " >= (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GE_ALL) {
				return StringBundler.concat(
					_propertyName, " >=ALL (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GE_SOME) {
				return StringBundler.concat(
					_propertyName, " >=SOME (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GT) {
				return StringBundler.concat(
					_propertyName, " > (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GT_ALL) {
				return StringBundler.concat(
					_propertyName, " >ALL (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.GT_SOME) {
				return StringBundler.concat(
					_propertyName, " >SOME (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.IN) {
				return StringBundler.concat(
					_propertyName, " in (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LE) {
				return StringBundler.concat(
					_propertyName, " <= (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LE_ALL) {
				return StringBundler.concat(
					_propertyName, " <=ALL (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LE_SOME) {
				return StringBundler.concat(
					_propertyName, " <=SOME (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LT) {
				return StringBundler.concat(
					_propertyName, " < (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LT_ALL) {
				return StringBundler.concat(
					_propertyName, " <ALL (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.LT_SOME) {
				return StringBundler.concat(
					_propertyName, " <SOME (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.NE) {
				return StringBundler.concat(
					_propertyName, " <> (", _dynamicQuery, ")");
			}
			else if (_criterionType == CriterionType.NOT_IN) {
				return StringBundler.concat(
					_propertyName, " not in (", _dynamicQuery, ")");
			}

			return "{criterionType=" + _criterionType + "}";
		}

		if (_targetPropertyName != null) {
			if (_criterionType == CriterionType.EQ) {
				return StringBundler.concat(
					_propertyName, "=", _targetPropertyName);
			}
			else if (_criterionType == CriterionType.GE) {
				return StringBundler.concat(
					_propertyName, ">=", _targetPropertyName);
			}
			else if (_criterionType == CriterionType.GT) {
				return StringBundler.concat(
					_propertyName, ">", _targetPropertyName);
			}
			else if (_criterionType == CriterionType.LE) {
				return StringBundler.concat(
					_propertyName, "<=", _targetPropertyName);
			}
			else if (_criterionType == CriterionType.LT) {
				return StringBundler.concat(
					_propertyName, "<", _targetPropertyName);
			}
			else if (_criterionType == CriterionType.NE) {
				return StringBundler.concat(
					_propertyName, "<>", _targetPropertyName);
			}

			return "{criterionType=" + _criterionType + "}";
		}

		if (_size != null) {
			if (_criterionType == CriterionType.EQ) {
				return StringBundler.concat(_propertyName, ".size=", _size);
			}
			else if (_criterionType == CriterionType.GE) {
				return StringBundler.concat(_propertyName, ".size>=", _size);
			}
			else if (_criterionType == CriterionType.GT) {
				return StringBundler.concat(_propertyName, ".size>", _size);
			}
			else if (_criterionType == CriterionType.LE) {
				return StringBundler.concat(_propertyName, ".size<=", _size);
			}
			else if (_criterionType == CriterionType.LT) {
				return StringBundler.concat(_propertyName, ".size<", _size);
			}
			else if (_criterionType == CriterionType.NE) {
				return StringBundler.concat(_propertyName, ".size<>", _size);
			}

			return "{criterionType=" + _criterionType + "}";
		}

		if (_criterionType == CriterionType.ALL_EQ) {
			return String.valueOf(_propertyNameValues);
		}
		else if (_criterionType == CriterionType.AND) {
			return StringBundler.concat(
				_criterions.get(0), " and ", _criterions.get(1));
		}
		else if (_criterionType == CriterionType.BETWEEN) {
			return StringBundler.concat(
				_propertyName, " between ", _values[0], " and ", _values[1]);
		}
		else if (_criterionType == CriterionType.EQ) {
			return StringBundler.concat(_propertyName, "=", _values[0]);
		}
		else if (_criterionType == CriterionType.GE) {
			return StringBundler.concat(_propertyName, ">=", _values[0]);
		}
		else if (_criterionType == CriterionType.GT) {
			return StringBundler.concat(_propertyName, ">", _values[0]);
		}
		else if (_criterionType == CriterionType.ILIKE) {
			return StringBundler.concat(_propertyName, " ilike ", _values[0]);
		}
		else if (_criterionType == CriterionType.IN) {
			return StringBundler.concat(
				_propertyName, " in (", StringUtil.merge(_values, ", "), ")");
		}
		else if (_criterionType == CriterionType.IS_EMPTY) {
			return _propertyName + " is empty";
		}
		else if (_criterionType == CriterionType.IS_NOT_EMPTY) {
			return _propertyName + " is not empty";
		}
		else if (_criterionType == CriterionType.IS_NOT_NULL) {
			return _propertyName + " is not null";
		}
		else if (_criterionType == CriterionType.IS_NULL) {
			return _propertyName + " is null";
		}
		else if (_criterionType == CriterionType.LE) {
			return StringBundler.concat(_propertyName, "<=", _values[0]);
		}
		else if (_criterionType == CriterionType.LIKE) {
			return StringBundler.concat(_propertyName, " like ", _values[0]);
		}
		else if (_criterionType == CriterionType.LT) {
			return StringBundler.concat(_propertyName, "<", _values[0]);
		}
		else if (_criterionType == CriterionType.NE) {
			return StringBundler.concat(_propertyName, "<>", _values[0]);
		}
		else if (_criterionType == CriterionType.NOT) {
			return "not " + _criterions.get(0);
		}
		else if (_criterionType == CriterionType.OR) {
			return StringBundler.concat(
				_criterions.get(0), " or ", _criterions.get(1));
		}
		else if (_criterionType == CriterionType.SQL_RESTRICTION) {
			return _sql;
		}

		return "{criterionType=" + _criterionType + "}";
	}

	private final CriterionType _criterionType;
	private List<Criterion> _criterions;
	private DynamicQuery _dynamicQuery;
	private String _propertyName;
	private Map<String, Criterion> _propertyNameValues;
	private Integer _size;
	private String _sql;
	private Type[] _sqlTypes;
	private String _targetPropertyName;
	private Object[] _values;

}