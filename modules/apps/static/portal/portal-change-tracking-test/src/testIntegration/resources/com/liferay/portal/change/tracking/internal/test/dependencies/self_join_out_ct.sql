SELECT
 MainTable.mainTableId, MainTable.ctCollectionId
FROM
 MainTable
LEFT JOIN
 MainTable tempMainTable
ON
 MainTable.mainTableId < tempMainTable.mainTableId AND
 (
  (
   MainTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
   (
    MainTable.ctCollectionId = 0 AND
    MainTable.mainTableId NOT IN (
     SELECT
      CTEntry.modelClassPK
     FROM
      CTEntry
     WHERE
      CTEntry.ctCollectionId = [$CT_COLLECTION_ID$] AND
      CTEntry.modelClassNameId = [$MAIN_TABLE_CLASS_NAME_ID$]
    )
   )
  ) OR
  MainTable.ctCollectionId IS NULL
 ) AND
 (
  (
   tempMainTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
   (
    tempMainTable.ctCollectionId = 0 AND
    tempMainTable.mainTableId NOT IN (
     SELECT
      CTEntry.modelClassPK
     FROM
      CTEntry
     WHERE
      CTEntry.ctCollectionId = [$CT_COLLECTION_ID$] AND
      CTEntry.modelClassNameId = [$MAIN_TABLE_CLASS_NAME_ID$]
    )
   )
  ) OR
  tempMainTable.ctCollectionId IS NULL
 )
WHERE
 tempMainTable.mainTableId IS NULL AND
 (
  (
   MainTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
   (
    MainTable.ctCollectionId = 0 AND
    MainTable.mainTableId NOT IN (
     SELECT
      CTEntry.modelClassPK
     FROM
      CTEntry
     WHERE
      CTEntry.ctCollectionId = [$CT_COLLECTION_ID$] AND
      CTEntry.modelClassNameId = [$MAIN_TABLE_CLASS_NAME_ID$]
    )
   )
  ) OR
  MainTable.ctCollectionId IS NULL
 )