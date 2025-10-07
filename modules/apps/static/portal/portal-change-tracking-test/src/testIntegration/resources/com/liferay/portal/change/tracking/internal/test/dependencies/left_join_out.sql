SELECT
 MainTable.mainTableId
FROM
 MainTable
LEFT JOIN
 ReferenceTable
ON
 ReferenceTable.mainTableId = MainTable.mainTableId AND
 (MainTable.ctCollectionId = 0 OR MainTable.ctCollectionId IS NULL) AND
 (ReferenceTable.ctCollectionId = 0 OR ReferenceTable.ctCollectionId IS NULL)
WHERE
 ReferenceTable.mainTableId IS NULL AND
 (MainTable.ctCollectionId = 0 OR MainTable.ctCollectionId IS NULL)
ORDER BY
 MainTable.mainTableId
ASC