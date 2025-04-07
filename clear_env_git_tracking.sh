#!/bin/bash

echo "Clearing Git tracking for environment files..."
git rm --cached .env .env.development .env.production .env.test
echo "Done! Environment files are now untracked but still in your working directory."