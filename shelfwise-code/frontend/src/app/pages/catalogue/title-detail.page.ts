import { ChangeDetectionStrategy, Component, effect, input, signal, untracked } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catalogueApi } from '../../core/api/catalogue.api';
import { deskApi } from '../../core/api/desk.api';
import { portalApi } from '../../core/api/portal.api';
import { BranchView, TitleResult } from '../../core/models';
import { createAction } from '../../helpers/async-action';
import { MATERIAL_LABEL } from '../../helpers/labels';
import { isDesk, isMember } from '../../store/auth.store';
import { contextMember, refreshMemberContext } from '../../store/member-context.store';
import { notifySuccess } from '../../store/notify.store';
import { refreshPortal } from '../../store/portal.store';
import { ErrorMessage } from '../../shared/error-message';
import { ReserveAction, ReserveRequest } from '../../shared/reserve-action';
import { TitleResultCard } from '../../shared/title-result-card';
import { PageHeader } from '../../ui/page-header';
import { SectionCard } from '../../ui/section-card';

/** One title from the public catalogue (F-CAT-01, F-CAT-04): its details, availability per branch and reserve. */
@Component({
  selector: 'app-title-detail-page',
  imports: [RouterLink, ErrorMessage, PageHeader, ReserveAction, SectionCard, TitleResultCard],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <p><a routerLink="/catalogue">← Back to the catalogue</a></p>
      <app-error-message [error]="loader.error() ?? booking.error()" />
      @if (title(); as t) {
        <app-page-header eyebrow="Catalogue" [title]="t.title" [subtitle]="t.authors" />
        <app-section-card title="Details">
          <dl class="details" data-testid="title-details">
            <dt>Material type</dt><dd>{{ material(t) }}</dd>
            <dt>Publisher</dt><dd>{{ t.publisher ?? '—' }}</dd>
            <dt>Publication year</dt><dd>{{ t.publicationYear ?? '—' }}</dd>
            <dt>ISBN</dt><dd class="mono">{{ t.isbn ?? '—' }}</dd>
          </dl>
        </app-section-card>
        @if (reservingFor(); as target) {
          <div class="info-box">Reservations made here are for <strong>{{ target }}</strong>.</div>
        }
        <app-title-result-card [title]="t">
          @if (canReserve()) {
            <app-reserve-action [titleId]="t.id" [branches]="branches()" [busy]="booking.loading()"
                                (reserve)="reserve($event)" />
          } @else if (!loggedIn()) {
            <p><a routerLink="/login">Log in</a> to reserve this title.</p>
          }
        </app-title-result-card>
      }
    </div>
  `,
  styles: `
    .details { display: grid; grid-template-columns: max-content 1fr; gap: var(--sw-space-2) var(--sw-space-5); margin: 0; }
    dt { color: var(--sw-color-muted); }
    dd { margin: 0; }
  `,
})
export class TitleDetailPage {
  /** `:id` from the route. */
  readonly id = input.required<string>();

  protected readonly title = signal<TitleResult | null>(null);
  protected readonly branches = signal<BranchView[]>([]);

  protected readonly loggedIn = () => isMember() || isDesk();
  protected readonly canReserve = () => isMember() || (isDesk() && contextMember() !== null);
  protected readonly reservingFor = () => {
    const member = contextMember();
    return isDesk() && member ? `${member.firstName} ${member.lastName}` : null;
  };

  protected readonly loader = createAction(async () => {
    this.title.set(await catalogueApi.title(Number(this.id())));
  });

  protected readonly booking = createAction(async (request: ReserveRequest) => {
    const member = contextMember();
    if (isDesk() && member) {
      await deskApi.reserve(member.id, request.titleId, request.pickupBranchId);
      await refreshMemberContext();
    } else {
      await portalApi.reserve(request.titleId, request.pickupBranchId);
      await refreshPortal();
    }
    notifySuccess('Reservation placed.');
    await this.loader.run();
  });

  constructor() {
    effect(() => {
      this.id();
      untracked(() => void this.loader.run());
    });
    catalogueApi.branches().then((list) => this.branches.set(list)).catch(() => this.branches.set([]));
  }

  protected material(t: TitleResult): string {
    return MATERIAL_LABEL[t.materialType];
  }

  protected reserve(request: ReserveRequest): void {
    void this.booking.run(request);
  }
}
