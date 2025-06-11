import { http, HttpResponse } from 'msw';
import { getImportErrorDetailResponseJSON, getImportSingleErrorDetailResponseJSON } from './mockResponses';

export const handlers = [

    http.get("/group/__mocks__/get-import-error-detail", (resolver) => {
        return HttpResponse.json(getImportErrorDetailResponseJSON, 200, {
            "Content-Type": "application/json"
        });
    }),

    http.get("/group/__mocks__/get-import-single-error-detail", (resolver) => {
          return HttpResponse.json(getImportSingleErrorDetailResponseJSON, 200, {
            "Content-Type": "application/json"
        });
    })
    
];

