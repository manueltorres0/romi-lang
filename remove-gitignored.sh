#!/bin/bash

# Remove files from git tracking that are now in .gitignore
# This removes them from remote but keeps them locally

git rm -r --cached .idea/
git rm -r --cached .vscode/
git rm --cached "*.iml"
git rm --cached "*.swp"
git rm --cached "*.swo"
git rm --cached "*~"
git rm --cached .DS_Store

git rm -r --cached 5Tests/
git rm -r --cached 7Tests/
git rm -r --cached 9Tests/
git rm -r --cached FinalTests/
git rm -r --cached JUnitTests/

git rm --cached "*.class"
git rm --cached "*.jar"
git rm --cached "*.war"
git rm --cached "*.ear"
git rm -r --cached target/
git rm -r --cached build/
git rm -r --cached out/
git rm -r --cached bin/

git rm --cached "*.log"
git rm --cached "*.tmp"
git rm --cached "*.bak"

git rm -r --cached testing

git commit -m "Remove gitignored files from tracking"
git push