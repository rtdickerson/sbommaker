#!/usr/bin/env python3
"""Generate a secure random API key for sbommaker."""

import argparse
import secrets


def main():
    parser = argparse.ArgumentParser(description="Generate a secure API key for sbommaker.")
    parser.add_argument(
        "--bytes", type=int, default=32,
        help="Number of random bytes (default: 32 → 43-char base64url key)"
    )
    args = parser.parse_args()

    if args.bytes < 16:
        parser.error("--bytes must be at least 16")

    key = secrets.token_urlsafe(args.bytes)
    print(key)
    print()
    print(f"Set in application.properties:")
    print(f"  app.api-key={key}")
    print()
    print(f"Or as an environment variable:")
    print(f"  APP_API_KEY={key}")


if __name__ == "__main__":
    main()
