package ${packagePath}.model.impl;

import ${serviceBuilder.getCompatJavaClassName("HashUtil")};
import ${serviceBuilder.getCompatJavaClassName("StringBundler")};

import ${apiPackagePath}.model.${entity.name};

<#if entity.hasCompoundPK()>
	import ${apiPackagePath}.service.persistence.${entity.name}PK;
</#if>

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Map;

/**
 * The cache model class for representing ${entity.name} in entity cache.
 *
 * @author ${author}
<#if classDeprecated>
 * @deprecated ${classDeprecatedComment}
</#if>
 * @generated
 */

<#if classDeprecated>
	@Deprecated
</#if>
public class ${entity.name}CacheModel implements CacheModel<${entity.name}>, Externalizable
	<#if entity.isMvccEnabled()>
		, MVCCModel
	</#if>

	{

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ${entity.name}CacheModel)) {
			return false;
		}

		${entity.name}CacheModel ${entity.variableName}CacheModel = (${entity.name}CacheModel)object;

		<#if entity.hasPrimitivePK(false)>
			<#if entity.isMvccEnabled()>
				if ((${entity.PKVariableName} == ${entity.variableName}CacheModel.${entity.PKVariableName}) && (mvccVersion == ${entity.variableName}CacheModel.mvccVersion)) {
			<#else>
				if (${entity.PKVariableName} == ${entity.variableName}CacheModel.${entity.PKVariableName}) {
			</#if>
		<#else>
			<#if entity.isMvccEnabled()>
				if (${entity.PKVariableName}.equals(${entity.variableName}CacheModel.${entity.PKVariableName}) && (mvccVersion == ${entity.variableName}CacheModel.mvccVersion)) {
			<#else>
				if (${entity.PKVariableName}.equals(${entity.variableName}CacheModel.${entity.PKVariableName})) {
			</#if>
		</#if>

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		<#if entity.isMvccEnabled()>
			int hashCode = HashUtil.hash(0, ${entity.PKVariableName});

			return HashUtil.hash(hashCode, mvccVersion);
		<#else>
			return HashUtil.hash(0, ${entity.PKVariableName});
		</#if>
	}

	<#if entity.isMvccEnabled()>
		@Override
		public long getMvccVersion() {
			return mvccVersion;
		}

		@Override
		public void setMvccVersion(long mvccVersion) {
			this.mvccVersion = mvccVersion;
		}
	</#if>

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(${(entity.regularEntityColumns?size - entity.blobEntityColumns?size) * 2 + 1});

		<#list entity.regularEntityColumns as entityColumn>
			<#if !stringUtil.equals(entityColumn.type, "Blob")>
				<#if entityColumn_index == 0>
					sb.append("{${entityColumn.name}=");
					sb.append(${entityColumn.name});
				<#elseif entityColumn_has_next>
					sb.append(", ${entityColumn.name}=");
					sb.append(${entityColumn.name});
				<#else>
					sb.append(", ${entityColumn.name}=");
					sb.append(${entityColumn.name});
					sb.append("}");
				</#if>
			</#if>
		</#list>

		return sb.toString();
	}

	@Override
	public ${entity.name} toEntityModel() {
		${entity.name}Impl ${entity.variableName}Impl = new ${entity.name}Impl();

		<#list entity.databaseRegularEntityColumns as entityColumn>
			<#if !stringUtil.equals(entityColumn.type, "Blob")>
				<#if stringUtil.equals(entityColumn.type, "Date")>
					if (${entityColumn.name} == Long.MIN_VALUE) {
						${entity.variableName}Impl.set${entityColumn.methodName}(null);
					}
					else {
						${entity.variableName}Impl.set${entityColumn.methodName}(new Date(${entityColumn.name}));
					}
				<#else>
					<#if stringUtil.equals(entityColumn.type, "String") && entityColumn.isConvertNull()>
						if (${entityColumn.name} == null) {
							${entity.variableName}Impl.set${entityColumn.methodName}("");
						}
						else {
							${entity.variableName}Impl.set${entityColumn.methodName}(${entityColumn.name});
						}
					<#else>
						${entity.variableName}Impl.set${entityColumn.methodName}(${entityColumn.name});
					</#if>
				</#if>
			</#if>
		</#list>

		${entity.variableName}Impl.resetOriginalValues();

		<#if cacheFields?size != 0>
			try {
				<#list cacheFields as cacheField>
					<#assign
						variableName = serviceBuilder.getVariableName(cacheField)
					/>

					_${variableName}MethodHandle.invokeExact(${entity.variableName}Impl, ${variableName});
				</#list>
			}
			catch (Throwable throwable) {
				ReflectionUtil.throwException(throwable);
			}
		</#if>

		return ${entity.variableName}Impl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws
		<#assign throwsClassNotFoundException = false />

		<#list entity.databaseRegularEntityColumns as entityColumn>
			<#if entityColumn.primitiveType>
			<#elseif stringUtil.equals(entityColumn.type, "Date")>
			<#elseif stringUtil.equals(entityColumn.type, "String") && !(serviceBuilder.getSqlType(entity.getName(), entityColumn) == "CLOB")>
			<#elseif !stringUtil.equals(entityColumn.type, "Blob")>
				<#assign throwsClassNotFoundException = true />
			</#if>
		</#list>

		<#if (cacheFields?size > 0)>
			<#assign throwsClassNotFoundException = true />
		</#if>

		<#if throwsClassNotFoundException>
			ClassNotFoundException,
		</#if>

		IOException {

		<#list entity.databaseRegularEntityColumns as entityColumn>
			<#if entityColumn.primitiveType>
				<#assign entityColumnPrimitiveType = serviceBuilder.getPrimitiveType(entityColumn.genericizedType) />

				${entityColumn.name} = objectInput.read${textFormatter.format(entityColumnPrimitiveType, 6)}();
			<#elseif stringUtil.equals(entityColumn.type, "Date")>
				${entityColumn.name} = objectInput.readLong();
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#if serviceBuilder.getSqlType(entity.getName(), entityColumn) == "CLOB">
					${entityColumn.name} = (String)objectInput.readObject();
				<#else>
					${entityColumn.name} = objectInput.readUTF();
				</#if>
			<#elseif !stringUtil.equals(entityColumn.type, "Blob")>
				${entityColumn.name} = (${entityColumn.genericizedType})objectInput.readObject();
			</#if>
		</#list>

		<#list cacheFields as cacheField>
			<#assign variableName = serviceBuilder.getVariableName(cacheField) />

			${variableName} = (${serviceBuilder.getGenericValue(cacheField.type)})objectInput.readObject();
		</#list>

		<#if entity.hasCompoundPK()>
			${entity.PKVariableName} = new ${entity.PKClassName}(

				<#list entity.PKEntityColumns as entityColumn>
					${entityColumn.name}

					<#if entityColumn_has_next>
						,
					</#if>
				</#list>

				);
		</#if>
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		<#list entity.databaseRegularEntityColumns as entityColumn>
			<#if entityColumn.primitiveType>
				<#assign entityColumnPrimitiveType = serviceBuilder.getPrimitiveType(entityColumn.genericizedType) />

				objectOutput.write${textFormatter.format(entityColumnPrimitiveType, 6)}(${entityColumn.name});
			<#elseif stringUtil.equals(entityColumn.type, "Date")>
				objectOutput.writeLong(${entityColumn.name});
			<#elseif stringUtil.equals(entityColumn.type, "String")>
				<#if serviceBuilder.getSqlType(entity.getName(), entityColumn) == "CLOB">
					if (${entityColumn.name} == null) {
						objectOutput.writeObject("");
					}
					else {
						objectOutput.writeObject(${entityColumn.name});
					}
				<#else>
					if (${entityColumn.name} == null) {
						objectOutput.writeUTF("");
					}
					else {
						objectOutput.writeUTF(${entityColumn.name});
					}
				</#if>

			<#elseif !stringUtil.equals(entityColumn.type, "Blob")>
				objectOutput.writeObject(${entityColumn.name});
			</#if>
		</#list>

		<#list cacheFields as cacheField>
			<#assign variableName = serviceBuilder.getVariableName(cacheField) />

			objectOutput.writeObject(${variableName});
		</#list>
	}

	<#list entity.databaseRegularEntityColumns as entityColumn>
		<#if !stringUtil.equals(entityColumn.type, "Blob")>
			<#if stringUtil.equals(entityColumn.type, "Date")>
				public long ${entityColumn.name};
			<#else>
				<#assign entityColumnPrimitiveType = serviceBuilder.getPrimitiveType(entityColumn.genericizedType) />

				public ${entityColumnPrimitiveType} ${entityColumn.name};
			</#if>
		</#if>
	</#list>

	<#list cacheFields as cacheField>
		<#assign variableName = serviceBuilder.getVariableName(cacheField) />

		public volatile ${serviceBuilder.getGenericValue(cacheField.type)} ${variableName};
	</#list>

	<#if entity.hasCompoundPK()>
		public transient ${entity.name}PK ${entity.PKVariableName};
	</#if>

	<#list cacheFields as cacheField>
		<#assign
			variableName = serviceBuilder.getVariableName(cacheField)
		/>

		private static final MethodHandle _${variableName}MethodHandle;
	</#list>

	<#if cacheFields?size != 0>
		static {
			MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

			try {
				<#list cacheFields as cacheField>
					<#assign
						variableName = serviceBuilder.getVariableName(cacheField)
					/>

					_${variableName}MethodHandle = lookup.findSetter(${entity.name}Impl.class, "${cacheField.name}", ${cacheField.type.canonicalName}.class);
				</#list>
			}
			catch (ReflectiveOperationException reflectiveOperationException) {
				throw new ExceptionInInitializerError(reflectiveOperationException);
			}
		}
	</#if>
}