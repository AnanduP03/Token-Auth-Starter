"use client";

import { signOut, useSession } from "next-auth/react";
import { logout } from "../serverActions/user/logout";
import { use, useEffect, useState } from "react";

export default function Page() {
  const { data } = useSession();

  async function handleSignout() {
    if (data) {
      let serverSignout = await logout();
      if (serverSignout.status === "success") {
        signOut();
      } else {
        console.log("Couldn't sign you out. Try again");
      }
    }
  }
  return (
    <>
      <div className="p-3 space-y-3 bg-[#3434346a] h-screen w-screen text-xl">
        <div className="">This is a protected Route</div>
        <button className="bg-red-500 p-2 rounded-lg" onClick={handleSignout}>
          Logout
        </button>
      </div>
    </>
  );
}
