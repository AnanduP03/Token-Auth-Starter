"use server";

export async function SignUp(
  username: string,
  password: string,
  email: string
) {
  const fetchData = fetch(
    `${process.env.NEXT_PUBLIC_BACKEND_ENDPOINT}/api/auth/register`,
    {
      method: "POST",
      body: JSON.stringify({
        username: username,
        email: email,
        password: password,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    }
  ).then((res) => {
    if (res.ok) {
      return { status: "success" };
    } else {
      return res.status === 500
        ? { status: "error", error: "Internal Server Error" }
        : res.json();
    }
  });

  return fetchData;
}
