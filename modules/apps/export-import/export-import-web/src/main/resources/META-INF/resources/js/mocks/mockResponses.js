export const getImportErrorDetailResponseJSON = {
    "actions": {},
    "facets": [],
    "items": [
        {
            "actions": {},
            "creator": {},
            "dateCreated": "2025-06-05T08:51:54Z",
            "dateModified": "2025-06-05T08:51:54Z",
            "externalReferenceCode": "ERC-1",
            "id": 51949,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Error Title 1",
            "entityType": "Employees",
            "errorType": "Missing Reference Exception",
            "errorEntityID": 12345,
            "errorExternalReferenceCode": "error-ERC-12345",
            "errorScope": "company",
            "errorSite": "Guest",
            "itemNumber": 1,
            "errorMessage": "This is an example error message 1.",
            "description": "Detailed description for error 1."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-2",
            "id": 51950,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Data Mismatch Issue",
            "entityType": "Products",
            "errorType": "Data Integrity Error",
            "errorEntityID": 23456,
            "errorExternalReferenceCode": "error-ERC-23456",
            "errorScope": "product_catalog",
            "errorSite": "Store A",
            "itemNumber": 2,
            "errorMessage": "Product ID mismatch in database record.",
            "description": "Validation failed for product attribute synchronization."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-3",
            "id": 51951,
            "status": {
                "code": 1,
                "label": "pending",
                "label_i18n": "Pending"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "API Timeout on User Sync",
            "entityType": "Users",
            "errorType": "API Timeout",
            "errorEntityID": 34567,
            "errorExternalReferenceCode": "error-ERC-34567",
            "errorScope": "user_management",
            "errorSite": "Admin Portal",
            "itemNumber": 3,
            "errorMessage": "External user synchronization API call timed out.",
            "description": "System failed to sync user data from external source due to latency."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-4",
            "id": 51952,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Invalid Email Format",
            "entityType": "Customers",
            "errorType": "Validation Error",
            "errorEntityID": 45678,
            "errorExternalReferenceCode": "error-ERC-45678",
            "errorScope": "customer_data",
            "errorSite": "CRM",
            "itemNumber": 4,
            "errorMessage": "Provided email address is not in a valid format.",
            "description": "User registration failed due to incorrect email input."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-5",
            "id": 51953,
            "status": {
                "code": 2,
                "label": "rejected",
                "label_i18n": "Rejected"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Payment Gateway Error",
            "entityType": "Orders",
            "errorType": "Payment Processing Failed",
            "errorEntityID": 56789,
            "errorExternalReferenceCode": "error-ERC-56789",
            "errorScope": "e-commerce",
            "errorSite": "Online Store",
            "itemNumber": 5,
            "errorMessage": "Payment gateway responded with a transaction decline.",
            "description": "Customer payment failed during checkout process."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-6",
            "id": 51954,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Inventory Shortage",
            "entityType": "Inventory",
            "errorType": "Stock Unavailable",
            "errorEntityID": 67890,
            "errorExternalReferenceCode": "error-ERC-67890",
            "errorScope": "warehouse",
            "errorSite": "Warehouse A",
            "itemNumber": 6,
            "errorMessage": "Requested quantity exceeds available stock for SKU X.",
            "description": "Order fulfillment halted due to insufficient inventory."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-7",
            "id": 51955,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Database Connection Lost",
            "entityType": "System",
            "errorType": "Database Error",
            "errorEntityID": 78901,
            "errorExternalReferenceCode": "error-ERC-78901",
            "errorScope": "backend",
            "errorSite": "Server Farm",
            "itemNumber": 7,
            "errorMessage": "Lost connection to primary database server.",
            "description": "Critical system process failed due to database connectivity issue."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-8",
            "id": 51956,
            "status": {
                "code": 1,
                "label": "pending",
                "label_i18n": "Pending"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "File Upload Failed",
            "entityType": "Documents",
            "errorType": "Storage Error",
            "errorEntityID": 89012,
            "errorExternalReferenceCode": "error-ERC-89012",
            "errorScope": "document_management",
            "errorSite": "User Dashboard",
            "itemNumber": 8,
            "errorMessage": "Unable to write file to cloud storage.",
            "description": "User document upload failed due to storage system error."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-9",
            "id": 51957,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Authentication Failure",
            "entityType": "Security",
            "errorType": "Login Failed",
            "errorEntityID": 90123,
            "errorExternalReferenceCode": "error-ERC-90123",
            "errorScope": "security",
            "errorSite": "Login Page",
            "itemNumber": 9,
            "errorMessage": "Invalid credentials provided for user authentication.",
            "description": "Multiple failed login attempts for a user account."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-10",
            "id": 51958,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Email Sending Failure",
            "entityType": "Notifications",
            "errorType": "SMTP Error",
            "errorEntityID": 1234,
            "errorExternalReferenceCode": "error-ERC-1234",
            "errorScope": "communication",
            "errorSite": "Backend Service",
            "itemNumber": 10,
            "errorMessage": "Failed to send email via SMTP server.",
            "description": "Automated email notification could not be delivered."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-11",
            "id": 51959,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Third-Party Service Offline",
            "entityType": "Integrations",
            "errorType": "External Service Unavailable",
            "errorEntityID": 2345,
            "errorExternalReferenceCode": "error-ERC-2345",
            "errorScope": "integrations",
            "errorSite": "API Gateway",
            "itemNumber": 11,
            "errorMessage": "Connection to external CRM service refused.",
            "description": "Integration with third-party CRM system is currently down."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-12",
            "id": 51960,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Indexing Failure",
            "entityType": "Search",
            "errorType": "Search Indexing Error",
            "errorEntityID": 3456,
            "errorExternalReferenceCode": "error-ERC-3456",
            "errorScope": "search_engine",
            "errorSite": "Search Service",
            "itemNumber": 12,
            "errorMessage": "Failed to index new content item.",
            "description": "New article not appearing in search results due to indexing problem."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-13",
            "id": 51961,
            "status": {
                "code": 1,
                "label": "pending",
                "label_i18n": "Pending"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Configuration Missing",
            "entityType": "Configuration",
            "errorType": "Missing Configuration",
            "errorEntityID": 4567,
            "errorExternalReferenceCode": "error-ERC-4567",
            "errorScope": "system_config",
            "errorSite": "Deployment",
            "itemNumber": 13,
            "errorMessage": "Required environment variable 'DB_HOST' is not set.",
            "description": "Application startup failed due to missing critical configuration."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-14",
            "id": 51962,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "License Expiration Warning",
            "entityType": "Licensing",
            "errorType": "License Issue",
            "errorEntityID": 5678,
            "errorExternalReferenceCode": "error-ERC-5678",
            "errorScope": "licensing",
            "errorSite": "License Server",
            "itemNumber": 14,
            "errorMessage": "Software license will expire in 30 days.",
            "description": "Alert: System license nearing expiration date."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-15",
            "id": 51963,
            "status": {
                "code": 2,
                "label": "rejected",
                "label_i18n": "Rejected"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Invalid Certificate",
            "entityType": "Security",
            "errorType": "SSL/TLS Error",
            "errorEntityID": 6789,
            "errorExternalReferenceCode": "error-ERC-6789",
            "errorScope": "network",
            "errorSite": "Load Balancer",
            "itemNumber": 15,
            "errorMessage": "SSL certificate for 'api.example.com' is invalid or expired.",
            "description": "Secure connection to external API failed due to certificate issue."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-16",
            "id": 51964,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Disk Space Critical",
            "entityType": "Infrastructure",
            "errorType": "Resource Exhaustion",
            "errorEntityID": 7890,
            "errorExternalReferenceCode": "error-ERC-7890",
            "errorScope": "server",
            "errorSite": "Data Center",
            "itemNumber": 16,
            "errorMessage": "Disk space on '/dev/sda1' is below 5% threshold.",
            "description": "Server running critically low on available disk space."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-17",
            "id": 51965,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Job Scheduler Failure",
            "entityType": "Jobs",
            "errorType": "Scheduler Error",
            "errorEntityID": 8901,
            "errorExternalReferenceCode": "error-ERC-8901",
            "errorScope": "automation",
            "errorSite": "Cron Server",
            "itemNumber": 17,
            "errorMessage": "Scheduled job 'daily_report_generator' failed to execute.",
            "description": "Automated task did not run as expected due to scheduler error."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-18",
            "id": 51966,
            "status": {
                "code": 1,
                "label": "pending",
                "label_i18n": "Pending"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Network Latency Detected",
            "entityType": "Network",
            "errorType": "Connectivity Issue",
            "errorEntityID": 9012,
            "errorExternalReferenceCode": "error-ERC-9012",
            "errorScope": "network_monitoring",
            "errorSite": "Edge Router",
            "itemNumber": 18,
            "errorMessage": "High network latency detected between data center and client.",
            "description": "Users experiencing slow response times due to network performance."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-19",
            "id": 51967,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "resolved",
                "name": "Resolved"
            },
            "title": "Invalid Data Type",
            "entityType": "Data Processing",
            "errorType": "Type Mismatch",
            "errorEntityID": 123,
            "errorExternalReferenceCode": "error-ERC-123",
            "errorScope": "data_pipeline",
            "errorSite": "ETL Process",
            "itemNumber": 19,
            "errorMessage": "Expected numeric value, received string for 'quantity' field.",
            "description": "Data transformation failed due to unexpected data type."
        },
        {
            "actions": {},
            "creator": {},
            "externalReferenceCode": "ERC-20",
            "id": 51968,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Service Degradation",
            "entityType": "Services",
            "errorType": "Performance Issue",
            "errorEntityID": 456,
            "errorExternalReferenceCode": "error-ERC-456",
            "errorScope": "monitoring",
            "errorSite": "Application Cluster",
            "itemNumber": 20,
            "errorMessage": "Response time for 'User Registration Service' exceeding threshold.",
            "description": "Degraded performance detected in core application service."
        }
    ],
    "lastPage": 1,
    "page": 1,
    "pageSize": 20,
    "totalCount": 20
};

export const getImportSingleErrorDetailResponseJSON = {
    "actions": {},
    "facets": [],
    "items": [
        {
            "actions": {},
            "creator": {},
            "dateCreated": "2025-06-05T08:51:54Z",
            "dateModified": "2025-06-05T08:51:54Z",
            "externalReferenceCode": "ERC-1",
            "id": 51949,
            "status": {
                "code": 0,
                "label": "approved",
                "label_i18n": "Approved"
            },
            "errorStatus": {
                "key": "unresolved",
                "name": "Unresolved"
            },
            "title": "Error Title 1",
            "entityType": "Employees",
            "errorType": "Missing Reference Exception",
            "errorEntityID": 12345,
            "errorExternalReferenceCode": "error-ERC-12345",
            "errorScope": "company",
            "errorSite": "Guest",
            "itemNumber": 1,
            "errorMessage": "This is an example error message 1.",
            "description": "Detailed description for error 1."
        }
    ],
    "lastPage": 1,
    "page": 1,
    "pageSize": 1,
    "totalCount": 1
};
