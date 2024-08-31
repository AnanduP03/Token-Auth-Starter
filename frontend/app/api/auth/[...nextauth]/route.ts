import NextAuth, { NextAuthOptions } from "next-auth";
import { JWT } from "next-auth/jwt";
import CredentialsProvider from "next-auth/providers/credentials";

async function refreshAccessToken(token: JWT) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_ENDPOINT}/api/auth/refreshToken`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          refreshToken: token.refreshToken,
        }),
      }
    );

    if (!res.ok) {
      throw res;
    }

    const data = await res.json();

    return {
      ...token,
      jwt: data.accessToken,
      refreshToken: data.refreshToken,
      jwtExpiry:
        Date.now() + parseInt(process.env.TOKEN_REFRESH_RATE as string),
    } as JWT;
  } catch (e: any) {
    return {
      ...token,
      error: "RefreshAccessTokenError",
    };
  }
}

export const authOptions: NextAuthOptions = {
  providers: [
    CredentialsProvider({
      name: "Credentials",
      credentials: {
        username: {
          label: "Username",
          type: "text",
          placeholder: "sample",
        },
        password: { label: "Password", type: "password" },
      },
      async authorize(credentials, req) {
        try {
          const res = await fetch(
            `${process.env.NEXT_PUBLIC_BACKEND_ENDPOINT}/api/auth/login`,
            {
              method: "POST",
              body: JSON.stringify({
                username: credentials?.username,
                password: credentials?.password,
              }),
              headers: { "Content-Type": "application/json" },
            }
          );

          if (!res.ok) {
            throw Error("Error while sending login request");
          }

          if (!credentials) {
            throw Error("No credentials found");
          }

          const parsedResponse = await res.json();

          const username = parsedResponse.username;
          const jwt = parsedResponse.accessToken;
          const refreshToken = parsedResponse.refreshToken;

          let user = {
            ...credentials,
            username,
            jwt,
            refreshToken,
          };
          return user;
        } catch (e: any) {}
      },
    }),
  ],

  secret: process.env.NEXTAUTH_SECRET,

  pages: {
    signIn: "/",
  },

  session: {
    strategy: "jwt",
  },

  callbacks: {
    async jwt({ token, user }) {
      if (user) {
        return {
          ...token,
          username: user.username,
          jwt: user.jwt,
          refreshToken: user.refreshToken,
          jwtExpiry:
            Date.now() + parseInt(process.env.TOKEN_REFRESH_RATE as string),
        };
      }

      if (Date.now() < token.jwtExpiry) {
        return token;
      }
      return await refreshAccessToken(token);
    },

    async session({ session, token }) {
      if (token) {
        session.user = token.username;
        session.jwt = token.jwt;
        session.refreshToken = token.refreshToken;
        session.error = token.error;
      }
      return session;
    },

    async redirect({ url, baseUrl }) {
      if (url.startsWith("/")) return `${baseUrl}${url}`;
      else if (new URL(url).origin === baseUrl) return url;
      return baseUrl;
    },
  },
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
