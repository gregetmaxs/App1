import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "App1",
  description: "A basic Next.js application",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
