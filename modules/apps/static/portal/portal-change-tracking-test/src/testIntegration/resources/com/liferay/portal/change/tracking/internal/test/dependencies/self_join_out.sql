SELECT
 MainTable.mainTableId, MainTable.ctCollectionId
FROM
 MainTable
LEFT JOIN
 MainTable tempMainTable
ON
 MainTable.mainTableId < tempMainTable.mainTableId AND
 (MainTable.ctCollectionId = 0 OR MainTable.ctCollectionId IS NULL) AND
 (tempMainTable.ctCollectionId = 0 OR tempMainTable.ctCollectionId IS NULL)
WHERE
 tempMainTable.mainTableId IS NULL AND
 (MainTable.ctCollectionId = 0 OR MainTable.ctCollectionId IS NULL)