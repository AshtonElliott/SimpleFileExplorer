<!--
HOW TO USE THIS FILE
- This is the structure we expect, not a form to pad out. It is the P2 document carried forward: sections
  1 to 3 of your P2 document are brought up to date, and the sections below (1 to 9) are new at this milestone.
- Guidance from the original template is kept in HTML comments like this one. They do not show when the
  file is rendered on GitHub or converted to PDF, but you can read them in the source. Delete them before
  the final commit. (In the original template these were the blue instruction boxes: "a document that still
  contains them has not been read by its own authors.")
- Length: aim for 14 to 20 pages of your own content, excluding appendices. Every section carries a budget.
  A section at twice its budget is padded, and padding is visible. A section at a quarter of its budget is
  usually missing the evidence, which costs more.
- Submit one PDF. The repository URL must appear inside the PDF, on the cover table below. If the grader
  cannot open your repository, the evidence in it does not exist for grading purposes.
- Section numbering follows the template; the rubric refers to it.

WHAT P3 DOES NOT ASK FOR
P3 is the end of the project, not an invitation to restart it. At this milestone we are NOT asking for:
- a rewritten requirements specification: sections 1 to 3 of P2 are updates, not new drafts
- new architecture work or new design alternatives: that was P2 and it is closed
- a complete test suite: we ask for risk-relevant tests with honest oracles, not coverage for its own sake
- a complete traceability matrix: an accurate matrix with honest gap rows scores above a
  complete-looking one that is untrue
- a polished product: a working subset you can explain beats a large one you cannot
A team that spends December rewriting P2 has spent its last three weeks in the wrong place and will not be
credited for it.
-->

## Project Deliverable P3: Final System, Testing, Report, and Demo

<!-- Team template: this is the same document you started at P1, grown to version 2.0 and final.
Weight 10% · 100 points · Due Wednesday 9 December 2026, 11:59 PM Central · One PDF per team on Canvas ·
Late work is not accepted -->

# Simple File Explorer

| | |
|---|---|
| **Project title** | Simple File Explorer |
| **Team number and section** | Team 4, Section .010 |
| **Team members (name · net ID)** | George Chakiath Dani * gnc230001, Austin Robson * axr230166 |
| **Repository URL** | https://github.com/AshtonElliott/SimpleFileExplorer/tree/main/p3 |
| **Release tag or final commit hash** | |
| **Demonstration slot** | |
| **Document version** | 2.0 |
| **Date** | |

---

## 0 Records

<!--
These tables are graded. The contribution record carries most of the individual 20% and must agree with
what a grader can actually open.
-->

### 0.1 Revision history

| Version | Date | Author | Summary of change |
|---|---|---|---|
| 1.9 | 11/8/2026 | Team 04 | Finalizing document sections and updated domain model |
| 2.0 | 11/19/2026 | Team 04 | P3 markdown creation, Initial additions, Getting P3 started |

### 0.2 Individual contribution record for P3

<!--
WHAT COUNTS HERE, STATED PLAINLY
- Name the artifact, not the activity. "Worked on testing" is worth nothing. "Wrote the partition table
  in 4.2 and tests T4-T9; commits a91c04, 4f1a9c" is worth full marks.
- Every claim in this table is checked against the repository as it stood at the deadline. A claim we
  cannot verify scores as if it were not made, and a claim we can show to be false is an integrity matter,
  not a grading one.
- If your team collaborated somewhere other than a Git host, you may still earn full marks: see 0.3 and
  Appendix A.

EXAMPLE (delete): Wrote 4.2-4.4 and tests T4-T9 in /tests/door/; reviewed PR #31 · Evidence: commits
a91c04, 4f1a9c; PR #28; review on PR #31 · Verifiable at: github.com/team07/cometlab
-->

| Team member | What this person produced for P3 (name the sections and files) | Evidence | Verifiable at |
|---|---|---|---|
| | | | |
| Ashton Elliott | created .gitignore file and removed .class files in repo | Commits b63435a and 9251bbe | Main Repo Page |
| George Chakiath Dani | Added initial P3 report content, including the revision history, contribution record, and initial final-scope documentation (Section 0 to Section 2) | Commit: 9732787 | GitHub repository  |
| George Chakiath Dani | Added initial content for Section 3, including build and run documentation with place holders for future changes, the component inventory, and deviations from the approved P2 design | Commit: 8b63e1c | GitHub repository |
| George Chakiath Dani | Drafted initial P3 report content for Sections 4 (Testing and Quality Evidence) and 5 (End-to-End Traceability), including test-risk analysis, input partitions and boundaries, testing/coverage placeholders, limitations, known defects, requirement traceability, and gap analysis. | Commit: c4a4b6e | GitHub repository  |


### 0.3 Where your evidence lives

<!--
TEAMS NOT USING GITHUB
A Git repository remains required for code, test sources and diagram sources. Collaboration on the written
document may happen elsewhere, provided you supply Appendix A: a per-member log (date, section, written or
reviewed) and version-history screenshots showing names and timestamps before the deadline.
A shared link on its own is not evidence. Access can change, and a link proves nothing about who wrote which
section. Documented edits are treated like commits and documented review comments like pull-request reviews.
-->

| | |
|---|---|
| **Repository URL (code, tests, diagram sources)** | https://github.com/AshtonElliott/SimpleFileExplorer/tree/main |
| **Access: public, or instructor has read access?** | Public GitHub repository  |
| **Document collaboration platform, if not Git** | N/A |
| **Appendix A supplied? (required if the row above is filled)** | N/A |

### 0.4 Changes since P2

One row per piece of P2 feedback you received, and what you did about it. "No change, and here is why" is an acceptable and often correct answer.

| P2 feedback or finding | What we changed | Where to see it |
|---|---|---|
| The domain model is populated with software and screen elements rather than domain concepts: GUI is a class whose attributes are 'directory display, search bar, quick access sidebar', and the class list runs GUI, search, sidebar, Error log, WSL translator. An analysis-level domain model carries no technology and no screen widgets - a file explorer's domain is files, folders, permissions and attributes. | | |
| The multiplicity notation ('GUI 1:1 Quick access', '1 Folder 1..*:1 File system') is not UML. | | |
| The non-trivial-step analysis lists the actor's own inputs as non-trivial. | | |
| And 5.3 reports six bare passes, which cannot all be true once 5.1 contains UI objects. | | |
| In section 6, QAS-01 allows ten minutes to search a folder of 1,000 files - that is not a quality target, and your own S-02 measured a competitor at 39 seconds. | | |
| 6.5 maps GUI to GUI, search to search and sidebar to sidebar, which means no architectural work happened between sections 5 and 6. | | |
| Section 11 is missing entirely. | | |

<!--
| P2 established the baseline architecture, requirements, design decisions, prototype evidence, and implementation for P3.| P3 builds upon the P2 design and moves the project toward its implemented and tested final version. | GitHub commits, source code, tests, and P3 documentation |
-->

---

## 1 Executive summary

<!--
Rubric criterion: all · budget: one page, and not more

WRITE THIS SECTION LAST
A reader who stops after this page should know what you built, what works, what does not, and what you
would do next. Write it after sections 3 to 6 exist, because until then you do not yet know what it says.
No background, no history of the team, no restating of the problem statement from P1.
-->

| | |
|---|---|
| **What we built, in two sentences** | Simple File Explorer (SFE) is a Java-based file exploration application designed to provide users with a way to browse directories and search through a graphical interface in a short amount of time. It uses java functions and command line tools to support file exploration while keeping file access read-only. |
<!--
| **What works end to end today** | TBD/ Work in progress - list the features that were verified successfully in the final application, including browsing, folder navigation, searching, and quick access, as applicable. |
| **What does not work, or was not built** | TBD/ Work in progress - list known bugs, incomplete features, unsupported functionality, and features excluded from the project scope. |
| **The single most important thing we learned** | Integrating a graphical interface with operating-system commands requires careful handling of command output, system dependencies, and the differences between the data returned by the operating system and the information displayed to the user. |

Commenting this out, because placeholder text risks being mistaken as complete text.
-->
| **What works end to end today** |  |
| **What does not work, or was not built** |  |
| **The single most important thing we learned** |  |

---

## 2 Final scope

<!-- Rubric criterion: Working system and implementation · 24 points · budget: 1/2 to 1 page -->

### 2.1 Delivered scope

List the requirements from your P2 baseline that the delivered system actually satisfies. Use your own requirement identifiers.

| Req ID | Requirement, in one line | Delivered? (Yes / Partly / No) |
|---|---|---|
| FR-01 | File information displayed in the GUI shall be accurate to its actual data. |  |
| FR-02 | Folders shall be navigable via mouse or keyboard actions. |  |
| FR-03 | Users shall search for strings in directory/file names and contents. | Partly - Work in Progress |
| FR-04 | Drives, commonly used folders, and folder history shall be displayed in a sidebar. | Partly - Work in Progress |

### 2.2 Dropped or deferred, and why 

<!--
THIS SECTION EARNS MARKS; IT DOES NOT LOSE THEM
Every real project cuts scope. Cutting scope deliberately and saying so is engineering judgement. Quietly
dropping a requirement and leaving it in the baseline is the thing that costs marks, because it makes the
rest of your document untrustworthy.
-->

| Req ID | What we dropped or deferred | Why, and what we did instead | 
|---|---|---|
| FR-05 | GUI error reporting |  |
| FR-06 | Image thumbnail generation and display | Thumbnails would require auxilliary processing and systems (image creation, caching, storage, etc) that were beyond the scope of this project. | 

---

## 3 The implemented system

<!-- Rubric criterion: Working system and implementation · 24 points · budget: 3 to 4 pages -->

### 3.1 Build and run from a clean clone

<!--
THE CLAIM BEHIND EVERY DEMONSTRATION
Write the instructions so that a person with none of your files, on a machine that is not yours, can get
the system running. Then have a teammate who did not write them follow them on their own machine, from an
empty directory, and record what happened in Appendix B.
A hard-coded path containing somebody's username is a defect. A step that lives only in one person's memory
is a defect. A credential in the repository is a serious one.
-->

| | |
|---|---|
| **Prerequisites and versions** |  |
| **Clone** | git clone https://github.com/AshtonElliott/SimpleFileExplorer.git |
| **Build (one command, if possible)** |  |
| **Run** |  |
| **Run the tests** |   |
| **Configuration not in the repository** |  |
| **Verified by (name, machine, date)** |   |
<!-- Specified the required Java version, operating system, WSL (If applicable), and any additional dependencies -->

### 3.2 Component inventory

One row per component from your P2 component view. If a P2 component has no code behind it, say so here rather than letting the traceability matrix discover it in section 5.

<!--
EXAMPLE (delete): Component: (from P2 section 6) · What it does: Decides whether a tap opens the door ·
Where the code is: /src/door/DoorController.java · State: Complete
-->

<!-- May need overhauling because actual application has developed differently -->

| Component (from P2 section 6) | What it does | Where the code is | State |
|---|---|---|---|
| GUI | Provides the graphical interface through which users interact with Simple File Explorer |  |  |
| File | Represents individual files and their associated information. |  |  |
| Folder | Represents directories and supports folder navigation. |  |  |
| File System | Provides access to the local file system and its contents |  |  |
| Search Results | Displays the results returned by the application's search functionality |  |  |
| Quick Access | Provides access to commonly used folders and navigation shortcuts |  |  |

State: use one of **Complete · Partial · Not built · Replaced** (the design said one thing, the code does another; explain in 3.3).

### 3.3 Deviations from the approved design

<!--
DRIFT IS NORMAL; UNDOCUMENTED DRIFT IS NOT
Where the code differs from the P2 design, say so here and give the reason. A deviation you declare is a
design decision. A deviation a grader finds in section 5 that you did not declare is a defect in your
documentation, and it is the single most common way teams lose points on criterion 3.
-->

| What the design said | What the code does | Why, and who decided |
|---|---|---|
| SFE would use linux commands to navigate folders and read files | Code uses no linux commands, nearly all functionality is done in java | Java proved capable enough to handle most functionality itself; Decided by Ashton |

---

## 4 Testing and quality evidence

<!--
Rubric criterion: Testing and quality evidence · 20 points · budget: 4 to 5 pages (the largest section)

WHAT THIS SECTION IS MARKED ON
Not the number of tests. Not a coverage percentage. This section is marked on whether a reader can tell what
you checked, where each expected result came from, what the tests found, and what they could not have found.
The rule that governs the whole section: every oracle must name its source. An expected value you derived by
running the code and writing down what it did is not an oracle. It agrees with your implementation by
construction, including where the implementation is wrong.
-->

### 4.1 What we chose to test, and why

You cannot test everything and nobody expects you to. Say what you judged to be risky, and test that. Three or four rows.

| What could go wrong | How bad would it be | What we did about it |
|---|---|---|
| Working Directory did not display all files | Fails FR-01 by displaying incomplete data | Tested file and folder information against the actual contents of the selected directory. |
| Folder navigation does not work consistently | Fails FR-02 by creating inconsistency with core functions | Tested navigation between directories and opening folders. |
| Search fails to return the correct results.	|	Users may be unable to locate files or folders. | Tested searches using different search terms and directory contents. |
| The application fails when an invalid path or inaccessible directory is encountered. | The application may crash or prevent users from continuing. | Tested invalid paths and error handling where applicable. |

### 4.2 Partitions and boundaries

<!--
FILL THE NONE ROWS IN: THEY ARE THE POINT
For each input that decides an outcome, list the classes the specification distinguishes and the boundary
values. Then name the test that exercises each one, or write NONE.
A table with no NONE rows in it is a table of the tests you happen to have, not an analysis of the input
space, and it will be marked as such.

EXAMPLE (delete): Input: card list age · Classes: Fresh (under 15 min) · stale (15 min or older) ·
Boundary values: 14:59 · 15:00 · 15:01 · Test ID, or NONE: T4, T5; 15:01 NONE
-->

| Input | Classes the specification distinguishes | Boundary values | Test ID, or NONE |
|---|---|---|---|
| File/folder path | Valid path, nonexistent path, inaccessible path, empty path | Root directory, empty path, invalid path | Pending verification |
| Search query | Matching query, no-match query, empty query | Empty string, single-character query, exact match | Pending verification |
| Directory contents | Empty directory, directory with files, directory with subfolders | Empty directory, one file, multiple files/folders | Pending verification |

### 4.3 Test inventory and oracle sources

<!--
An oracle with no source named does not count. Acceptable sources: a numbered requirement, a non-functional
requirement, an architecture decision record, or a numbered step of a use-case realization. Not acceptable:
"expected: true", "matches current behaviour", or a value read off a run.

EXAMPLE (delete): T1 · Stale list, valid card · Door refuses; one REFUSED_STALE record · ADR-3 +
realization step 2 · Pass
-->

| ID | What it exercises | Oracle: the expected result | Source of that oracle | Result |
|---|---|---|---|---|
| Pending | Pending identification of actual test cases. | Pending verification. | Requirements / test specification. | Not yet verified. |

### 4.4 Results

| | |
|---|---|
| **Tests written / passing / failing** | Pending verification. |
| **How the suite is run** | Pending verification. |
| **Any test that is flaky, and why** | Pending verification. |
| **Defects these tests actually found** | Pending verification. |

### 4.5 Two numbers, not one

<!--
A SINGLE COVERAGE PERCENTAGE IS NOT A QUALITY RESULT
Report a coverage figure AND something about the strength of your oracles. If your toolchain can run
mutation analysis, give the mutation score. If it cannot, give an honest count: how many of your tests
contain no assertion at all, and how many assert only that nothing threw.
Then write the sentence that makes the pair defensible: one sentence saying what the two numbers together
tell a reader, and what they do not.
-->

| Measure | Value | What it does and does not tell a reader |
|---|---|---|
| Statement or line coverage | Pending verification. | Indicates how much code was executed during testing, but does not establish that all behavior is correct. |
| Branch coverage (if available) | Pending verification. | Indicates how many decision branches were exercised by the tests. |
| Mutation score, or tests with no assertion | Pending verification. | Helps assess whether tests detect changes or failures in program behavior rather than merely executing code. |
| Our one-sentence reading of the pair | Pending verification. | Summarizes the significance and limitations of the coverage results. |

### 4.6 What this suite cannot catch

<!--
REQUIRED. ONE PARAGRAPH, AND IT MUST BE SPECIFIC
Name a class of defect your tests would miss, and say why. Generic answers score nothing: "our suite is
comprehensive" is the clearest possible signal that nobody looked.
Candidates worth considering: design defects that the code implements faithfully; concurrency and timing;
performance and the behaviour of the system under load or with a dependency down; anything whose oracle you
derived from your own code; and anything in 4.2 marked NONE.
-->
**(Temporary Draft..)**
The test suite cannot guarantee that Simple File Explorer will behave correctly in every environment or for every possible file system configuration. Testing is limited to the inputs, directories, operating system environments, and scenarios covered by the available tests. Unexpected file system conditions, inaccessible directories, unusual file types, or other untested cases may still cause errors or unexpected behavior.

### 4.7 Known defects and limitations

A defect you report costs you nothing. A defect the grader finds that you did not report costs you twice: once on this criterion and once on criterion 3.

| ID | What is wrong | Effect on the user | Why it is still here |
|---|---|---|---|
| Pending identification | Defects and limitations have not yet been fully documented. | Pending review of the implemented system. | Pending team review. |

---

## 5 End-to-end traceability

<!--
Rubric criterion: Traceability, documentation, report, and limitations · 18 points · budget: 2 to 3 pages

ACCURACY, NOT COMPLETENESS
This is the single artifact that connects everything you have produced this semester. One row per
requirement or architecture decision that matters, carried from the requirement all the way to the evidence
that it works.
An honest NOT IMPLEMENTED, DRIFTS or ORPHAN row scores above a complete-looking row that is untrue. We check
a sample of rows against your repository. A row that does not survive that check costs more than the row
was worth.
-->

### 5.1 The matrix

<!--
EXAMPLE (delete): NFR-12 · A revoked card must never open a door · Design element: DoorController, step 3 ·
Code: DoorController.java:41 · Evidence: test T7; commit 4f1a9c · Status: OK
-->

| ID | Requirement or decision | Design element | Code | Evidence | Status |
|---|---|---|---|---|---|
| FR-01 | Accurate file information in GUI | File / GUI components | Pending identification | Pending testing evidence | NO EVIDENCE |
| FR-02 | Navigate folders using mouse and keyboard | GUI / Folder / File system components | Pending identification | Pending testing evidence | NO EVIDENCE |
| FR-03 | Search strings in directory/file names and contents | Search component | Pending identification | Pending testing evidence | NO EVIDENCE |
| FR-04 | Sidebar for drives, commonly used folders, and folder history | GUI / Quick Access components | Pending identification | Pending identification | NO EVIDENCE |
| FR-05 | Report errors in GUI | GUI / error-handling components | Pending identification | Pending testing evidence | NO EVIDENCE |
| FR-06 | Display images with thumbnails | GUI / File components | Pending identification | Pending testing evidence | NO EVIDENCE |
| FR-07 | Create/delete/rename files | Not applicable if excluded from scope | Pending identification | P2 scope decision | NOT IMPLEMENTED |
| FR-08 | Move files | File / GUI components | Not applicable if excluded from scope | P2 scope decision | NOT IMPLEMENTED |

Status vocabulary: **OK** (requirement, design, code and evidence agree) · **PARTIAL** (built, but not fully) · **DRIFTS** (the code does something the design did not say) · **ORPHAN** (code with no requirement behind it) · **NOT IMPLEMENTED** · **NO EVIDENCE** (it may work; nothing shows that it does)

### 5.2 Gap analysis

Take every row above that is not OK and say, in one line each, what it means for the delivered system. This is where the matrix stops being a table and becomes an argument.

| ID | What the gap is | What it means for the user, and what we would do next |
|---|---|---|
| Pending | Identify requirements marked PARTIAL, DRIFTS, ORPHAN, NOT IMPLEMENTED, or NO EVIDENCE. | Describe the impact and the proposed improvement or next step.  |

---

## 6 Limitations, risks, and what we would do next

<!-- Rubric criterion: Traceability, documentation, report, and limitations · 18 points · budget: 1/2 to 1 page -->

Three to five items. Each one: the limitation, the risk it creates, and the first thing you would do about it with another two weeks.

| Limitation | Risk it creates | First thing we would do next |
|---|---|---|
| | | |

---

## 7 Demonstration plan

<!--
Rubric criterion: Demonstration and professional communication · 18 points · budget: 1/2 page

THE DEMONSTRATION IS LIVE AND IN CLASS
Eight to ten minutes of presentation, then about five minutes of questions. Every member speaks, and every
member must be able to answer a question about any part of the system, not only their own part.
A separate preparation guide and the full demonstration rubric are posted alongside this template. Read them
before you build your slides; they are what you are marked against.
-->

| | |
|---|---|
| **The end-to-end scenario we will show** | |
| **Who presents which part** | |
| **What we will show live vs. from a recording** | |
| **Fallback if the live run fails** | |
| **Data we will use (no real personal data, no credentials)** | |

---

## 8 Individual evidence

<!--
Rubric criterion: Individual evidence, AI-use log, and defense · 20 points · budget: 1/2 page per member

EACH MEMBER WRITES THEIR OWN ENTRY, IN THEIR OWN WORDS
One short paragraph each. Name what you personally produced, point at where a grader can verify it, and
state one thing you got wrong and fixed. An entry that could have been written about anybody on the team is
worth very little.
Copying another member's entry with the names changed is visible and is treated as an integrity matter.
-->

| Member | What I produced · where to verify it · one thing I got wrong and fixed |
|---|---|
| | |

---

## 9 AI-use log

<!--
Rubric criterion: Individual evidence, AI-use log, and defense · 20 points · budget: 1/2 to 1 page

HONEST DISCLOSURE IS GRADED; NON-DISCLOSURE IS AN INTEGRITY MATTER
Declaring that you used an AI tool costs you nothing. Using one and not declaring it, or declaring it in a
way that does not match your commit history, is a different kind of problem.
You must be able to explain every artifact you submit, including anything a tool produced. "The tool wrote
it" is not an answer at the demonstration. If a generated test or a generated section cannot be explained by
the person who submitted it, it is marked as if it were not there.
If your team used no AI tools at all, write one line saying so and sign it. That is a complete and
acceptable log.

EXAMPLE (delete): Member: (name) · Tool and version: Claude, Nov 2026 · What it was used for: Drafted the
JUnit scaffolding for T4-T6 · How the output was verified: Rewrote every oracle from ADR-3; two generated
assertions were wrong and were replaced
-->

 |---|---|---|---|
| Austin Robson | Claude | Convert P3 template to markdown | Full readthrough to verify document structure before pushing to github |
| George Chakiath Dani | ChatGPT | Used to formulate ideas, organize initial P3 documentation, clarify report content, and reference the requirements, design decisions, and information established in P1 and P2. | Reviewed and checked the suggestions against P1 and P2 and the team's P3 report to ensure consistency and accuracy. |
| George Chakiath Dani | ChatGPT | Used to organize and draft the initial P3 Section 3 documentation, including build and run instructions, the component inventory, and deviations from the approved P2 design. | Reviewed the suggested content and wording to ensure consistency with the P3 template and the requirements established in P1 and P2. |
| George Chakiath Dani | ChatGPT | Assist with organizing and drafting P3 Sections 4 and 5 | Reviewed the drafted content to ensure it aligned with the P3 template and report structure before committing to GitHub. |

---

## 10 Appendices

<!-- Appendices do not count against your page budget, and they are not a place to move required content to. -->

### Appendix A: Collaboration evidence

<!--
Required only for teams whose written document was not produced in a Git repository. Per-member log plus
version-history screenshots with names and pre-deadline timestamps.
-->

| Member | Date | Section written or reviewed | Where this is shown in the screenshots |
|---|---|---|---|
| | | | |

### Appendix B: Clean-clone build transcript

<!--
Paste the actual terminal session from 3.1, run by a member who did not write the instructions, on a machine
that is not the one the project was developed on. Include the failures if there were any; a transcript with a
failure you then fixed is more credible than one without.
-->

```text
[terminal session]
```

### Appendix C: Repository map

<!-- A short tree of the top two levels, so a grader knows where to look. -->

```text
[top two levels of the repository]
```

<!--
SUBMISSION CHECKLIST: tick every line before you upload
- One PDF, uploaded to Canvas by one member, before 11:59 PM Central on Wednesday 9 December.
- The repository URL is on the cover table and the grader can open it.
- Every blue instruction box has been deleted.
- Every 4.2 row has a test ID or the word NONE.
- Every oracle in 4.3 names its source.
- Every 5.1 row has a status, and every non-OK row appears in 5.2.
- 4.6 names a specific class of defect, not a general statement.
- Each member wrote their own 8 entry.
- 9 exists, even if it says no AI tools were used.
- No credentials, no real personal data, and no unapproved third-party content anywhere in the PDF or the
  repository.
-->

<!--
=====================================================================
HOW P3 IS GRADED: 100 POINTS
=====================================================================
Four criteria (80 points) are shared by the team. The fifth (20 points) is yours alone and is not averaged
with your team's.

Working system and implementation: 24
  Implemented scope is coherent, maintainable, and consistent with approved design.
Testing and quality evidence: 20
  Risk-relevant tests, clear oracles, results, defects and limits, and reproducible evidence.
Traceability, documentation, report, and limitations: 18
  Accurate end-to-end traceability and professional final documentation.
Demonstration and professional communication: 18
  Focused, credible demonstration; team can explain decisions and evidence.
Individual evidence, AI-use log, and defense: 20
  Verifiable contribution, honest disclosure, output verification, and individual command of the work.
-->
