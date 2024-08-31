import { getToken } from "next-auth/jwt";
import withAuth from "next-auth/middleware";
import { NextResponse } from "next/server";

export default withAuth(
  async function middleware(req) {
    const token = await getToken({ req });
    if (!token) {
      const url = new URL("/api/auth/signIn", req.url);
      url.searchParams.set("callbackUrl", "/error");
      return NextResponse.redirect(url);
    }

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_BACKEND_ENDPOINT}/api/auth/authorizationCheck`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token.jwt}`,
          },
        }
      );

      if (!res.ok) {
        if (typeof window !== "undefined") {
          window.sessionStorage.clear();
        }

        let redirect = NextResponse.redirect(new URL("/", req.url));
        redirect.cookies.delete("next-auth.callback-url");
        redirect.cookies.delete("next-auth.csrf-token");
        redirect.cookies.delete("next-auth.session-token");
        return redirect;
      }
    } catch (e) {
      throw new Error("Couldn't reach backend server");
    }

    if (token.error === "RefreshAccessTokenError") {
      const url = new URL("/", req.url);
      return NextResponse.redirect(url);
    }
  },
  {
    callbacks: {
      authorized: ({ token }) => !!token,
    },
  }
);

export const config = {
  matcher: ["/user/:path*"],
};
