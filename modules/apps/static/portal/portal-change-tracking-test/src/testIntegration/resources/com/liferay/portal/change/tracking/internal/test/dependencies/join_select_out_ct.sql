SELECT
 mainTable.mainTableId, mainTable.companyId, mainTable.groupId, mainTable.name, mainTable.ctCollectionId
FROM
 MainTable mainTable
INNER JOIN
 ReferenceTable referenceTable
ON
 referenceTable.mainTableId = mainTable.mainTableId
WHERE
 referenceTable.name = ? AND
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
 ) AND
 (
  referenceTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
  (
   referenceTable.ctCollectionId = 0 AND
   referenceTable.referenceTableId NOT IN (
    SELECT
     CTEntry.modelClassPK
    FROM
     CTEntry
    WHERE
     CTEntry.ctCollectionId = [$CT_COLLECTION_ID$] AND
     CTEntry.modelClassNameId = [$REFERENCE_TABLE_CLASS_NAME_ID$]
   )
  )
 )
ORDER BY
 mainTable.mainTableId ASC