<#list entities as entity>
	<import class="${apiPackagePath}.model.${entity.name}" />
</#list>

<#list entities as entity>
	<class
		<#if entity.isDynamicUpdateEnabled()>
			dynamic-update="true"
		</#if>

		name="${packagePath}.model.impl.${entity.name}Impl" table="${entity.table}"
	>
		<#if entity.hasCompoundPK()>
			<composite-id class="${apiPackagePath}.service.persistence.${entity.name}PK" name="primaryKey">
				<#list entity.PKEntityColumns as entityColumn>
					<key-property

					<#if serviceBuilder.isVersionGTE_7_4_0()>
						access="com.liferay.portal.dao.orm.hibernate.PublicFieldPropertyAccessor"
					<#elseif serviceBuilder.isHBMCamelCasePropertyAccessor(entityColumn.name)>
						access="com.liferay.portal.dao.orm.hibernate.CamelCasePropertyAccessor"
					<#elseif serviceBuilder.isVersionGTE_7_1_0()>
						access="com.liferay.portal.dao.orm.hibernate.LiferayPropertyAccessor"
					</#if>

					<#if entityColumn.name != entityColumn.DBName>
						column="${entityColumn.DBName}"
					</#if>

					name="${entityColumn.name}"

					<#if entityColumn.isPrimitiveType() || stringUtil.equals(entityColumn.type, "Map") || stringUtil.equals(entityColumn.type, "String")>
						type="com.liferay.portal.dao.orm.hibernate.${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}Type"
					</#if>

					<#if stringUtil.equals(entityColumn.type, "Date")>
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							type="timestamp"
						<#else>
							type="org.hibernate.type.TimestampType"
						</#if>
					</#if>

					/>
				</#list>
			</composite-id>
		<#else>
			<#assign entityColumn = entity.PKEntityColumns?first />

			<id
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					access="com.liferay.portal.dao.orm.hibernate.MethodPropertyAccessor"
				<#elseif serviceBuilder.isHBMCamelCasePropertyAccessor(entityColumn.name)>
					access="com.liferay.portal.dao.orm.hibernate.CamelCasePropertyAccessor"
				<#elseif serviceBuilder.isVersionGTE_7_1_0()>
					access="com.liferay.portal.dao.orm.hibernate.LiferayPropertyAccessor"
				</#if>

				<#if entityColumn.name != entityColumn.DBName>
					column="${entityColumn.DBName}"
				</#if>

				name="${entityColumn.name}"
				type="<#if !entity.hasPrimitivePK()>java.lang.</#if>${entityColumn.type}"

				>

				<#if entityColumn.idType??>
					<#assign class = serviceBuilder.getGeneratorClass("${entityColumn.idType}") />

					<#if stringUtil.equals(class, "class")>
						<#assign class = entityColumn.idParam />
					</#if>
				<#else>
					<#assign class = "assigned" />
				</#if>

				<generator
					class="${class}"

					<#if stringUtil.equals(class, "sequence")>
						<#if serviceBuilder.isVersionGTE_7_4_0()>
							><param name="sequence_name">${entityColumn.idParam}</param>
						<#else>
							><param name="sequence">${entityColumn.idParam}</param>
						</#if>
						</generator>
					<#else>
						/>
					</#if>
			</id>
		</#if>

		<#if entity.isMvccEnabled()>
			<version
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					access="com.liferay.portal.dao.orm.hibernate.PrivateFieldPropertyAccessor"
				<#else>
					access="com.liferay.portal.dao.orm.hibernate.PrivatePropertyAccessor"
				</#if>
				name="mvccVersion" type="long" />
		</#if>

		<#list entity.databaseRegularEntityColumns as entityColumn>
			<#if !entityColumn.isPrimary() && !entityColumn.entityName?? && (!stringUtil.equals(entityColumn.type, "Blob") || (stringUtil.equals(entityColumn.type, "Blob") && !entityColumn.lazy)) && !stringUtil.equals(entityColumn.name, "mvccVersion")>
				<property

				<#if serviceBuilder.isVersionGTE_7_4_0()>
					access="com.liferay.portal.dao.orm.hibernate.MethodPropertyAccessor"
				<#elseif !entityColumn.isInterfaceColumn()>
					access="com.liferay.portal.dao.orm.hibernate.PrivatePropertyAccessor"
				<#elseif serviceBuilder.isHBMCamelCasePropertyAccessor(entityColumn.name)>
					access="com.liferay.portal.dao.orm.hibernate.CamelCasePropertyAccessor"
				<#elseif serviceBuilder.isVersionGTE_7_1_0()>
					access="com.liferay.portal.dao.orm.hibernate.LiferayPropertyAccessor"
				</#if>

				<#if entityColumn.name != entityColumn.DBName>
					column="${entityColumn.DBName}"
				</#if>

				name="${entityColumn.name}"

				<#if (serviceBuilder.getSqlType(entity.getName(), entityColumn) == "CLOB") && !stringUtil.equals(entityColumn.type, "Map")>
					type="com.liferay.portal.dao.orm.hibernate.StringClobType"
				<#elseif entityColumn.isPrimitiveType() || stringUtil.equals(entityColumn.type, "Map") || stringUtil.equals(entityColumn.type, "String")>
					type="com.liferay.portal.dao.orm.hibernate.${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}Type"
				<#elseif serviceBuilder.isVersionGTE_7_4_0()>
					<#if stringUtil.equals(entityColumn.type, "BigDecimal")>
						type="big_decimal"
					<#elseif stringUtil.equals(entityColumn.type, "Blob")>
						type="blob"
					<#elseif stringUtil.equals(entityColumn.type, "Date")>
						type="timestamp"
					</#if>
				<#else>
					<#if stringUtil.equals(entityColumn.type, "Date")>
						type="org.hibernate.type.TimestampType"
					<#else>
						type="org.hibernate.type.${entityColumn.type}Type"
					</#if>
				</#if>

				/>
			</#if>

			<#if stringUtil.equals(entityColumn.type, "Blob") && entityColumn.lazy>
				<#if serviceBuilder.isVersionGTE_7_4_0()>
					<#assign constrained = "false" />
				<#else>
					<#assign constrained = "true" />
				</#if>

				<one-to-one
					<#if serviceBuilder.isVersionGTE_7_4_0()>
						access="com.liferay.portal.dao.orm.hibernate.PrivateFieldPropertyAccessor"
						cascade="merge,persist,save-update"
					<#else>
						access="com.liferay.portal.dao.orm.hibernate.PrivatePropertyAccessor"
						cascade="save-update"
					</#if>
					class="${apiPackagePath}.model.${entity.name}${entityColumn.methodName}BlobModel" constrained="${constrained}" name="${entityColumn.name}BlobModel" outer-join="false" />
			</#if>
		</#list>
	</class>

	<#list entity.blobEntityColumns as blobEntityColumn>
		<#if blobEntityColumn.lazy>
			<class
				<#if entity.isDynamicUpdateEnabled()>
					dynamic-update="true"
				</#if>

				lazy="true" name="${apiPackagePath}.model.${entity.name}${blobEntityColumn.methodName}BlobModel" table="${entity.table}"
			>
				<#assign entityColumn = entity.PKEntityColumns?first />

				<#if entity.hasCompoundPK()>
					<composite-id class="${apiPackagePath}.service.persistence.${entity.name}PK" name="${entity.PKClassName}">
						<#list entity.PKEntityColumns as entityColumn>
							<key-property

							<#if serviceBuilder.isVersionGTE_7_4_0()>
								access="com.liferay.portal.dao.orm.hibernate.PublicFieldPropertyAccessor"
							<#elseif serviceBuilder.isHBMCamelCasePropertyAccessor(entityColumn.name)>
								access="com.liferay.portal.dao.orm.hibernate.CamelCasePropertyAccessor"
							<#elseif serviceBuilder.isVersionGTE_7_1_0()>
								access="com.liferay.portal.dao.orm.hibernate.LiferayPropertyAccessor"
							</#if>

							<#if entityColumn.name != entityColumn.DBName>
								column="${entityColumn.DBName}"
							</#if>

							name="${entityColumn.name}"

							<#if entityColumn.isPrimitiveType() || stringUtil.equals(entityColumn.type, "Map") || stringUtil.equals(entityColumn.type, "String")>
								type="com.liferay.portal.dao.orm.hibernate.${serviceBuilder.getPrimitiveObj("${entityColumn.type}")}Type"
							</#if>

							<#if stringUtil.equals(entityColumn.type, "Date")>
								<#if serviceBuilder.isVersionGTE_7_4_0()>
									type="timestamp"
								<#else>
									type="org.hibernate.type.TimestampType"
								</#if>
							</#if>

							/>
						</#list>
					</composite-id>
				<#else>
					<id column="${entityColumn.DBName}" name="${entityColumn.name}">
						<generator class="foreign">
							<param name="property">${packagePath}.model.impl.${entity.name}Impl</param>
						</generator>
					</id>
				</#if>

				<property column="${blobEntityColumn.DBName}" name="${blobEntityColumn.name}Blob" type="blob" />
			</class>
		</#if>
	</#list>
</#list>