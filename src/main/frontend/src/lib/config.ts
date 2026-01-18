export type AppConfig = {
  apiBaseUrl: string;
};

function resolveApiBaseUrl(): string {
  const fromEnv = import.meta.env?.VITE_API_BASE_URL as string | undefined;
  // Default to '/api' so it works behind the Spring Boot proxy in production
  return fromEnv && fromEnv.length > 0 ? fromEnv : '/api';
}

export const config: AppConfig = {
  apiBaseUrl: resolveApiBaseUrl(),
};
