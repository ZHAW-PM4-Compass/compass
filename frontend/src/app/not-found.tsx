'use client';

import { useRouter } from "next/navigation";

export default function LandingPage() {
    const router = useRouter();
    router.push('/home');
}