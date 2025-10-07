SELECT
 mainTable.mainTableId, mainTable.companyId, mainTable.groupId, mainTable.name, mainTable.ctCollectionId
FROM
 MainTable mainTable
WHERE
 mainTable.groupId = ? AND
 (
  mainTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
  (
   mainTable.ctCollectionId = 0 AND
   mainTable.mainTableId NOT IN (
    SELECT
     CTEntry.modelClassPK
    FROM
     CTEntry
    WHERE
     CTEntry.ctCollectionId = [$CT_COLLECTION_ID$] AND
     CTEntry.modelClassNameId = [$MAIN_TABLE_CLASS_NAME_ID$]
    )
  )
 )
ORDER BY
 mainTable.mainTableId ASC