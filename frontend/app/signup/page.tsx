"use client";

import { SignUp } from "@/app/serverActions/auth/signup";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useRef } from "react";

export default function Page() {
  const usernameRef = useRef<HTMLInputElement>();
  const passwordRef = useRef<HTMLInputElement>();
  const emailRef = useRef<HTMLInputElement>();

  const router = useRouter();

  async function onSubmit(event) {
    event.preventDefault();
    const res = await SignUp(
      usernameRef.current.value,
      passwordRef.current.value,
      emailRef.current.value
    );

    if (res.status === "success") {
      router.push("/");
    } else {
      throw Error("Couldn't signup");
    }
  }
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
            <div className="text-[#454545] font-semibold">Email</div>
            <input
              type="text"
              ref={emailRef}
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
              Sign up
            </button>
          </div>
          <div className="text-base flex justify-end ">
            <div className="">
              <Link
                href={"/"}
                className="text-[#454545] hover:text-[#232323] transition-all"
              >
                Already have an account?
              </Link>
            </div>
          </div>
        </form>
      </div>
    </>
  );
}
