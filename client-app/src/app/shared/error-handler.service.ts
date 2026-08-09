import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../core/models/api.model';

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {
  getMessages(error: unknown): string[] {
    if (error instanceof HttpErrorResponse) {
      const body = error.error;

      if (body && typeof body === 'object') {
        const apiError = body as ErrorResponse;
        if (apiError.errorMessages?.length) {
          return apiError.errorMessages;
        }

        const springError = body as { error?: string; message?: string; errorMessage?: string | string[] };
        if (springError.message) return [springError.message];
        if (springError.error) return [springError.error];
        if (springError.errorMessage) {
          return Array.isArray(springError.errorMessage) ? springError.errorMessage : [springError.errorMessage];
        }
      }

      if (typeof body === 'string' && body.trim()) {
        try {
          const parsed = JSON.parse(body) as ErrorResponse;
          if (parsed.errorMessages?.length) return parsed.errorMessages;
        } catch {
          return [body];
        }
      }

      if (error.status === 0) {
        return ['Unable to reach the server. Please ensure the backend is running and try again.'];
      }

      if (error.status === 403) {
        return ['Access denied. You do not have permission to perform this action.'];
      }

      return [error.message || 'An unexpected error occurred'];
    }

    if (error instanceof Error && error.message) {
      return [error.message];
    }

    return ['An unexpected error occurred'];
  }
}
