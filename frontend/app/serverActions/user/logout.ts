"use server";

import { authOptions } from "@/app/api/auth/[...nextauth]/route";
import { getServerSession } from "next-auth";

export async function logout() {
  const session = await getServerSession(authOptions);

  if (!session) {
    throw Error("Session not available");
  }

  const fetchData = fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_ENDPOINT}/api/auth/logout`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${session.jwt}`,
      },
    }
  ).then((res) => {
    if (res.ok) {
      return { status: "success" };
    } else {
      return res.status === 500
        ? {
            status: "error",
            error: "Internal Server Error",
          }
        : res.json();
    }
  });

  return fetchData;
}
