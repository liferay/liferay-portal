SELECT
 COUNT(*)
FROM
 MainTable
WHERE
 MainTable.mainTableId IN (
  SELECT
   mainTableId
  FROM
   ReferenceTable
  WHERE
   ReferenceTable.name = ? AND
   (
    ReferenceTable.ctCollectionId = [$CT_COLLECTION_ID$] OR
    (
     ReferenceTable.ctCollectionId = 0 AND
     ReferenceTable.referenceTableId NOT IN (
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
 ) AND
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
 )