# Contributing

Rules the team follows when working on this repository.

## Branch naming

Every branch must be created from an existing ticket (GitHub Issue) and follow this pattern:

```
feat/<ticket-number>-<short-description> 
fix/<ticket-number>-<short-description>
```

Examples:
```
feat/14-unit-tests-service
fix/9-cascade-delete-bug
```

## Workflow

1. Pick a ticket from the Kanban board, move it to "In Progress".
2. Create a branch from `main` using the naming pattern above.
3. Commit your work on that branch.
4. Open a Pull Request into `main`.
5. At least one teammate reviews and approves the PR before it gets merged.
6. Move the ticket to "Done" on the Kanban board.

## Commits

Write commit messages that describe *what* changed, not just "update" or "fix".

