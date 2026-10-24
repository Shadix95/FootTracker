const competitionSelect = document.getElementById('competition');
let currentStandings = [];

function competition() {
    return competitionSelect.value;
}

async function callApi(url, method = 'GET') {
    const response = await fetch(url, { method });
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Erreur ' + response.status }));
        throw new Error(error.message);
    }
    if (response.status === 204) {
        return null;
    }
    const contentType = response.headers.get('Content-Type') || '';
    return contentType.includes('json') ? response.json() : response.text();
}

function showMessage(text, type = 'danger') {
    document.getElementById('message').innerHTML =
        `<div class="alert alert-${type} alert-dismissible">${escapeHtml(text)}
            <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
         </div>`;
}

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;');
}

function formBadges(results) {
    const labels = { W: ['V', 'success'], D: ['N', 'secondary'], L: ['D', 'danger'] };
    return results.map(r => `<span class="badge bg-${labels[r][1]} me-1">${labels[r][0]}</span>`).join('');
}

function matchLine(match) {
    if (!match) {
        return '<span class="text-muted">-</span>';
    }
    const score = match.homeScore === null ? 'vs' : `${match.homeScore} - ${match.awayScore}`;
    const date = new Date(match.date).toLocaleDateString('fr-FR');
    return `${escapeHtml(match.homeTeam)} <strong>${score}</strong> ${escapeHtml(match.awayTeam)}
            <small class="text-muted">(J${match.matchday}, ${date})</small>`;
}

function statCard(label, value) {
    return `<div class="col-6 col-md-3">
                <div class="card text-center"><div class="card-body py-2">
                    <div class="fs-4 fw-bold">${value}</div><div class="small text-muted">${label}</div>
                </div></div>
            </div>`;
}

async function loadStandings() {
    currentStandings = await callApi(`/api/competitions/${competition()}/standings`);
    document.getElementById('team-details').innerHTML = '';

    if (currentStandings.length === 0) {
        document.getElementById('standings-body').innerHTML =
            '<tr><td colspan="11" class="text-center text-muted">Aucune donnée : clique sur "Synchroniser".</td></tr>';
    } else {
        document.getElementById('standings-body').innerHTML = currentStandings.map(row => `
            <tr>
                <td>${row.position}</td>
                <td><a href="#" onclick="showTeamDetails(${row.teamId}).catch(e => showMessage(e.message)); return false;">${escapeHtml(row.teamName)}</a></td>
                <td>${row.played}</td><td>${row.wins}</td><td>${row.draws}</td><td>${row.losses}</td>
                <td>${row.goalsFor}</td><td>${row.goalsAgainst}</td><td>${row.goalDifference}</td>
                <td><strong>${row.points}</strong></td>
                <td><button class="btn btn-sm btn-outline-warning py-0" title="Ajouter aux favoris"
                            onclick="addFavorite(${row.teamId})">★</button></td>
            </tr>`).join('');
    }
    fillTeamSelects();
}

async function showTeamDetails(teamId) {
    const [stats, evolution] = await Promise.all([
        callApi(`/api/competitions/${competition()}/teams/${teamId}/stats`),
        callApi(`/api/competitions/${competition()}/teams/${teamId}/ranking-evolution`)
    ]);

    const recordRow = (label, r) => `<tr><td>${label}</td><td>${r.played}</td><td>${r.wins}</td><td>${r.draws}</td>
        <td>${r.losses}</td><td>${r.goalsFor}</td><td>${r.goalsAgainst}</td><td>${r.points}</td></tr>`;

    document.getElementById('team-details').innerHTML = `
        <div class="card mb-3">
            <div class="card-header d-flex justify-content-between">
                <strong>${escapeHtml(stats.teamName)}</strong>
                <button class="btn-close" onclick="document.getElementById('team-details').innerHTML = ''"></button>
            </div>
            <div class="card-body">
                <div class="row g-2 mb-3">
                    ${statCard('Buts marqués / match', stats.goalsForPerMatch)}
                    ${statCard('Buts encaissés / match', stats.goalsAgainstPerMatch)}
                    ${statCard('Clean sheets', stats.cleanSheets)}
                    ${statCard('Forme', formBadges(stats.lastFiveResults) || '-')}
                </div>
                <table class="table table-sm">
                    <thead><tr><th></th><th>J</th><th>V</th><th>N</th><th>D</th><th>BP</th><th>BC</th><th>Pts</th></tr></thead>
                    <tbody>
                        ${recordRow('Total', stats.total)}
                        ${recordRow('Domicile', stats.home)}
                        ${recordRow('Extérieur', stats.away)}
                    </tbody>
                </table>
                <div class="small text-muted mb-1">Position après chaque journée</div>
                <div>${evolution.map(p => `<span class="badge text-bg-light border me-1 mb-1">J${p.matchday} : ${p.position}e</span>`).join('')}</div>
            </div>
        </div>`;
}

async function loadScorers() {
    const scorers = await callApi(`/api/competitions/${competition()}/scorers/stats`);
    document.getElementById('scorers-body').innerHTML = scorers.length === 0
        ? '<tr><td colspan="8" class="text-center text-muted">Aucune donnée.</td></tr>'
        : scorers.map(s => `
            <tr>
                <td>${escapeHtml(s.playerName)}</td><td>${escapeHtml(s.teamName)}</td>
                <td>${s.playedMatches}</td><td><strong>${s.goals}</strong></td><td>${s.penalties}</td>
                <td>${s.nonPenaltyGoals}</td><td>${s.goalsPerMatch}</td><td>${s.shareOfTeamGoals} %</td>
            </tr>`).join('');
}

async function loadMatchday(matchday) {
    const s = await callApi(`/api/competitions/${competition()}/matchdays/${matchday}/summary`);
    document.getElementById('matchday-result').innerHTML = `
        <div class="row g-2 mb-3">
            ${statCard('Matchs joués', s.matchesPlayed + (s.matchesRemaining ? ` (${s.matchesRemaining} à venir)` : ''))}
            ${statCard('Buts', s.totalGoals)}
            ${statCard('Buts / match', s.goalsPerMatch)}
            ${statCard('Dom. / Nuls / Ext.', `${s.homeWins} / ${s.draws} / ${s.awayWins}`)}
        </div>
        <p><strong>Plus large victoire :</strong> ${matchLine(s.biggestWin)}</p>
        <p><strong>Match le plus prolifique :</strong> ${matchLine(s.highestScoringMatch)}</p>
        <ul class="list-group">${s.matches.map(m => `<li class="list-group-item">${matchLine(m)}</li>`).join('')}</ul>`;
}

function fillTeamSelects() {
    const options = currentStandings
        .map(row => `<option value="${row.teamId}">${escapeHtml(row.teamName)}</option>`)
        .join('');
    document.getElementById('h2h-team1').innerHTML = options;
    document.getElementById('h2h-team2').innerHTML = options;
    if (currentStandings.length > 1) {
        document.getElementById('h2h-team2').selectedIndex = 1;
    }
}

async function loadHeadToHead(team1, team2) {
    const h = await callApi(`/api/competitions/${competition()}/head-to-head?team1=${team1}&team2=${team2}`);
    document.getElementById('h2h-result').innerHTML = `
        <div class="row g-2 mb-3">
            ${statCard('Victoires ' + escapeHtml(h.team1), h.team1Wins)}
            ${statCard('Nuls', h.draws)}
            ${statCard('Victoires ' + escapeHtml(h.team2), h.team2Wins)}
            ${statCard('Buts', `${h.team1Goals} - ${h.team2Goals}`)}
        </div>
        ${h.matchesPlayed === 0
            ? '<p class="text-muted">Pas encore de confrontation cette saison.</p>'
            : `<ul class="list-group">${h.matches.map(m => `<li class="list-group-item">${matchLine(m)}</li>`).join('')}</ul>`}`;
}

async function loadFavorites() {
    const favorites = await callApi('/api/favorites');
    document.getElementById('favorites-list').innerHTML = favorites.length === 0
        ? '<p class="text-muted">Aucun favori : ajoute une équipe avec ★ dans le classement.</p>'
        : favorites.map(f => `
            <div class="col-md-6">
                <div class="card h-100">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h5 class="mb-0">
                                ${f.crest ? `<img src="${escapeHtml(f.crest)}" alt="" width="28" class="me-2">` : ''}
                                ${escapeHtml(f.teamName)}
                            </h5>
                            <button class="btn btn-sm btn-outline-danger" onclick="removeFavorite(${f.teamId})">Retirer</button>
                        </div>
                        <p class="mb-1">Forme : ${formBadges(f.lastFiveResults) || '-'}</p>
                        <p class="mb-1 small">Dernier match : ${matchLine(f.lastMatch)}</p>
                        <p class="mb-0 small">Prochain match : ${matchLine(f.nextMatch)}</p>
                    </div>
                </div>
            </div>`).join('');
}

async function addFavorite(teamId) {
    try {
        const favorite = await callApi(`/api/favorites/${teamId}`, 'POST');
        showMessage(`${favorite.teamName} ajouté aux favoris`, 'success');
    } catch (e) {
        showMessage(e.message);
    }
}

async function removeFavorite(teamId) {
    try {
        await callApi(`/api/favorites/${teamId}`, 'DELETE');
        await loadFavorites();
    } catch (e) {
        showMessage(e.message);
    }
}

async function synchronize() {
    const button = document.getElementById('sync-button');
    button.disabled = true;
    button.textContent = 'Synchronisation...';
    try {
        const result = await callApi(`/api/sync/${competition()}`, 'POST');
        showMessage(result, 'success');
        await loadStandings();
    } catch (e) {
        showMessage(e.message);
    } finally {
        button.disabled = false;
        button.textContent = 'Synchroniser';
    }
}

const tabLoaders = {
    standings: loadStandings,
    scorers: loadScorers,
    matchday: () => {},
    'head-to-head': () => {},
    favorites: loadFavorites
};
let currentTab = 'standings';

async function openTab(tab) {
    currentTab = tab;
    document.querySelectorAll('[data-tab]').forEach(b => b.classList.toggle('active', b.dataset.tab === tab));
    document.querySelectorAll('.tab').forEach(s => s.classList.toggle('d-none', s.id !== 'tab-' + tab));
    try {
        await tabLoaders[tab]();
    } catch (e) {
        showMessage(e.message);
    }
}

document.querySelectorAll('[data-tab]').forEach(button =>
    button.addEventListener('click', () => openTab(button.dataset.tab)));

competitionSelect.addEventListener('change', async () => {
    document.getElementById('matchday-result').innerHTML = '';
    document.getElementById('h2h-result').innerHTML = '';
    try {
        if (currentTab !== 'standings') {
            await loadStandings();
        }
        await openTab(currentTab);
    } catch (e) {
        showMessage(e.message);
    }
});

document.getElementById('sync-button').addEventListener('click', synchronize);

document.getElementById('matchday-form').addEventListener('submit', async event => {
    event.preventDefault();
    try {
        await loadMatchday(document.getElementById('matchday-input').value);
    } catch (e) {
        showMessage(e.message);
    }
});

document.getElementById('h2h-form').addEventListener('submit', async event => {
    event.preventDefault();
    try {
        await loadHeadToHead(document.getElementById('h2h-team1').value, document.getElementById('h2h-team2').value);
    } catch (e) {
        showMessage(e.message);
    }
});

openTab('standings');
