# Logistics — Kotlin Mobile Code Challenge

Android app that **intakes consignment records** from a **mock upstream gateway**, **persists them locally**, applies **validation and de-duplication**, maintains a **sync queue** for valid rows, and drives **status transitions** through to **sync success or failure**, with **retry** for failed deliveries. The UI is a small **delivery-style dashboard** for running the pipeline and inspecting rows.

This project is structured to match the exercise goals: **clear layering**, **coroutines**, **explicit domain state**, and **unit tests** on core rules—without a real backend.

---

## Screenshots

<img width="540" height="1200" alt="Screen1" src="https://github.com/user-attachments/assets/73072fcb-1cef-440a-a30c-f6585cb8c57e" />
<img width="540" height="1200" alt="Screen2" src="https://github.com/user-attachments/assets/e187a1f4-cf95-4a0f-a69f-1899a1fb5e4c" />
<img width="540" height="1200" alt="Screen3" src="https://github.com/user-attachments/assets/6d7152b8-2136-47f2-b8c6-9c8cecbad03d" />

---

## How to run

**Prerequisites:** Android Studio (recent stable), JDK 17 (or the JDK bundled with Android Studio), Android SDK.

1. Clone the repo and open the **`Logistics`** folder in Android Studio.
2. Let Gradle sync finish.
3. Run the **`app`** configuration on an emulator or device (**Run**).
4. On the **Delivery Dashboard**:
   - **Import orders** — loads `sample_consignments.json` via the mock gateway, saves to Room, validates each row.
   - **Send to driver queue** — enqueues **validated** rows only.
   - **Mark delivery updates** — processes the queue with a **mock outbound** client (simulated success/failure).
   - **Clear all data** — wipes local DB so you can repeat the flow from a clean state.

**Command line (optional):**

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

---

## Challenge requirements → implementation

| Requirement | What was done |
|-------------|-------------|
| Read inbound records from JSON via a **mock interface** | `ConsignmentGateway` + `MockConsignmentGateway` reads `app/src/main/assets/sample_consignments.json`. |
| **Persist locally** | **Room**: `local_consignment` + `sync_queue` (`ConsignmentDatabase`, DAOs, `ConsignmentRepositoryImpl`). |
| **Reject duplicates** (business key / `consignmentId`) | First row with a given ID wins; later rows with the same trimmed `consignmentId` are skipped (`IngestOutcome.DuplicateSkipped`). |
| **Validate** required fields | `ConsignmentValidator`: `consignmentId`, `customerCode`, `itemCount`, `status` (inbound `status` string). Invalid rows get `validationState = INVALID`, `status = INVALID`, and a **failure reason** string. |
| **Invalid records not queued** | Only `VALIDATED` + valid validation state are enqueued (`enqueueAllValidated` / policy helpers). |
| **Sync queue** for valid / ready work | `SyncQueueItemEntity` + `SyncQueueDao`; one queue row per consignment when dispatched. |
| **Statuses** | `NEW` → (`VALIDATED` \| `INVALID`) → `QUEUED` → (`SYNCED` \| `FAILED`). Documented in `StatusTransitionPolicy` and enforced in the repository. |
| **Retry failed** | `RetryFailedConsignmentUseCase` / `retryFailed`: failed rows can be re-queued for another sync attempt. |
| **Kotlin, separation of concerns, coroutines** | Use cases (`Intake`, `Enqueue`, `ProcessSync`, `Retry`, `Clear`, `Observe`), repository abstraction, ViewModel + Compose UI. |
| **Tests for core logic** | See **Testing** below (`ConsignmentValidatorTest`, `ConsignmentRepositoryImplTest`, `StatusTransitionPolicyTest`). |

---

## Architecture (short)

- **`domain/`** — entities, validation, gateway/repository **ports**, sync port, transition policy.
- **`data/`** — Room, gateway DTO mapping, `MockConsignmentGateway`, `MockOutboundSyncClient`, `ConsignmentRepositoryImpl`.
- **`usecase/`** — application-specific operations (intake, enqueue, sync, retry, clear, observe).
- **`ui/`** — Compose screens, theme, ViewModel.
- **`di/`** — `AppContainer` wires dependencies.

**Replacing the mock gateway later:** implement `ConsignmentGateway.fetchInboundRecords()` with a real HTTP/GraphQL client, map responses to `InboundConsignment`, and swap the binding in `DefaultAppContainer`. The rest of the pipeline can stay the same.

---

## Sample payload

Bundled asset: **`app/src/main/assets/sample_consignments.json`**

Includes valid rows, a **duplicate** `consignmentId`, rows that **fail validation** (e.g. empty customer, zero items), and data suitable for exercising the flow end-to-end.

---

## Testing

Unit tests under **`app/src/test/`**:

- **`ConsignmentValidatorTest`** — valid / invalid inputs and reasons.
- **`ConsignmentRepositoryImplTest`** — persistence, intake, enqueue, sync, retry (Room in-memory).
- **`StatusTransitionPolicyTest`** — allowed transitions and queue/sync invariants.

Run:

```bash
./gradlew :app:testDebugUnitTest
```

---

## Assumptions & demo behavior

- **Duplicate rule:** same **trimmed `consignmentId`** as an existing row → duplicate skipped; **first stored row is kept** (later payload for that ID is not applied as a second active record).
- **Sync failure demo:** `MockOutboundSyncClient` fails when the **business key** contains **`fail`** (case-insensitive), so you can exercise **FAILED** and **Retry delivery** in the UI.
- **Clear data:** **Clear all data** removes all rows from Room (`clearAllTables`) so you can restart the demo without reinstalling.
- **No real network:** all “inbound” and “outbound” I/O is local or simulated; suitable for offline review and CI-friendly unit tests.

---
