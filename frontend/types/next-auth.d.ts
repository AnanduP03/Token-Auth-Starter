import NextAuth, { DefaultSession } from "next-auth";
import { JWT } from "next-auth/jwt";

declare module "next-auth" {
  interface User {
    username: string;
    password?: string;
    email: string;
    jwt: string;
    refreshToken: string;
  }

  interface Session {
    jwt: string;
    refreshToken: string;
    error?: string;
    expires?: string;
    user?: string;
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    jwt: string;
    refreshToken: string;
    error: string;
    jwtExpiry: number;
  }
}
