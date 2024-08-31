"use client";

import { signIn } from "next-auth/react";
import Link from "next/link";
import { useRef } from "react";

export default function Page() {
  const usernameRef = useRef<HTMLInputElement>();
  const passwordRef = useRef<HTMLInputElement>();

  const onSubmit = async () => {
    event.preventDefault();
    const result = await signIn("credentials", {
      username: usernameRef.current.value,
      password: passwordRef.current.value,
      callbackUrl: "/user",
    });
  };

  return (
    <>
      <div className="bg-[#3434346a] h-screen w-screen flex justify-center items-center text-lg">
        <form
          onSubmit={onSubmit}
          className="flex flex-col gap-3 border border-[#4545453a] shadow-lg p-5 rounded-lg tracking-wide"
        >
          <div className="">
            <div className="text-[#454545] font-semibold">Username</div>
            <input
              type="text"
              ref={usernameRef}
              className="bg-[#5656566a] p-1 rounded-lg w-[15rem] text-[#f6f6f6]"
            />
          </div>
          <div className="">
            <div className="text-[#454545] font-semibold">Password</div>
            <input
              type="password"
              ref={passwordRef}
              className="bg-[#5656566a] p-1 rounded-lg w-[15rem] text-[#f6f6f6]"
            />
          </div>
          <div className="">
            <button
              className=" transition-all border-2 p-2 w-full rounded-lg border-[#4545455a] hover:border-[#4343433a] text-[#454545] hover:text-[#545454]"
              type="submit"
            >
              Sign in
            </button>
          </div>
          <div className="text-base flex justify-end ">
            <div className="">
              <Link
                href={"/signup"}
                className="text-[#454545] hover:text-[#232323] transition-all"
              >
                New User?
              </Link>
            </div>
          </div>
        </form>
      </div>
    </>
  );
}
