import {test,expect} from '@playwright/test';
async function login(page:any,email='admin@maosemacao.local',password='DemoGestor2026!') {
  await page.goto('/');await expect(page.locator('.login-form')).toHaveCSS('max-width','430px');await page.getByLabel('E-mail',{exact:true}).fill(email);await page.getByLabel('Senha',{exact:true}).fill(password);await page.getByRole('button',{name:'Entrar na plataforma'}).click();
  await expect(page.getByRole('heading',{name:'Juntos, fazemos acontecer.'})).toBeVisible();
}
test('gestão cria campanha, meta, publica, recebe doação e entrega',async({page})=>{
  await login(page);const name='Campanha E2E '+Date.now();
  await page.getByRole('button',{name:'Campanhas',exact:true}).click();await page.getByRole('button',{name:'+ Nova campanha'}).click();
  const dialog=page.getByRole('dialog');await dialog.getByLabel('Nome da campanha').fill(name);await dialog.getByLabel('Propósito e descrição').fill('Arrecadação testada pelo navegador.');await dialog.getByRole('button',{name:'Salvar',exact:true}).click();
  const card=page.locator('article.campaign').filter({hasText:name});await expect(card).toBeVisible();await card.getByRole('button',{name:'Necessidades'}).click();
  await dialog.getByLabel('Meta total').fill('50');await dialog.getByRole('button',{name:'Salvar',exact:true}).click();await expect(dialog).not.toBeVisible();await card.getByRole('button',{name:'Publicar',exact:true}).click();await expect(card).toContainText('PUBLICADA');
  await page.getByRole('button',{name:'Estoque',exact:true}).click();await page.getByRole('button',{name:'+ Registrar doação'}).click();await dialog.getByRole('combobox',{name:'Campanha',exact:true}).selectOption({label:name});await dialog.getByLabel('Quantidade recebida').fill('20');await dialog.getByLabel('Origem / justificativa').fill('Parceiro E2E');await dialog.getByRole('button',{name:'Salvar',exact:true}).click();await expect(page.getByRole('cell',{name:'Parceiro E2E',exact:true}).last()).toBeVisible();
  await page.getByRole('button',{name:'Entregas',exact:true}).click();await page.getByRole('button',{name:'+ Registrar entrega'}).click();await dialog.getByRole('combobox',{name:'Campanha',exact:true}).selectOption({label:name});await dialog.getByLabel('Quantidade',{exact:true}).fill('3');await dialog.getByLabel('Código da família / instituição').fill('FAM-E2E-'+name);await dialog.getByRole('button',{name:'Salvar',exact:true}).click();await expect(dialog).not.toBeVisible();
  const row=page.getByRole('row').filter({hasText:'FAM-E2E-'+name});await expect(row).toContainText('ENTREGUE');await row.getByRole('button',{name:'Estornar'}).click();await dialog.getByLabel('Justificativa do estorno').fill('Devolução testada');await dialog.getByRole('button',{name:'Salvar',exact:true}).click();await expect(row).toContainText('ESTORNADA');
});
test('cadastro, aprovação, inscrição, alteração e notificação',async({browser})=>{
  const email='e2e-'+Date.now()+'@example.test';const password='VoluntarioTeste123!';
  const volunteerContext=await browser.newContext();const page=await volunteerContext.newPage();await page.goto('/');await page.getByRole('button',{name:'Quero ser voluntário'}).click();await page.getByLabel('Seu nome').fill('Voluntário E2E');await page.getByLabel('E-mail',{exact:true}).fill(email);await page.getByLabel('Senha',{exact:true}).fill(password);await page.getByRole('button',{name:'Criar minha conta'}).click();await expect(page.getByRole('status')).toContainText('Cadastro enviado');
  const adminContext=await browser.newContext();const admin=await adminContext.newPage();await login(admin);await admin.getByRole('button',{name:'Pessoas',exact:true}).click();const row=admin.getByRole('row').filter({hasText:email});await row.getByRole('button',{name:'Aprovar cadastro'}).click();await expect(row).toContainText('Aprovado');
  await login(page,email,password);await page.getByRole('button',{name:'Voluntariado',exact:true}).click();const shift=page.locator('article.shift').first();await shift.getByRole('button',{name:'Quero participar'}).click();await expect(shift.getByRole('button',{name:'Cancelar minha inscrição'})).toBeVisible();
  await admin.getByRole('button',{name:'Voluntariado',exact:true}).click();await admin.locator('article.shift').first().getByRole('button',{name:'Editar',exact:true}).click();const dialog=admin.getByRole('dialog');await dialog.getByLabel('Atividade do turno').fill('Organização — atualização E2E');await dialog.getByRole('button',{name:'Salvar',exact:true}).click();await expect(dialog).not.toBeVisible();
  await page.reload();await page.getByRole('button',{name:/^Notificações/}).click();await expect(page.getByText(/Turno alterado/)).toBeVisible();await page.getByRole('button',{name:'Marcar como lida'}).first().click();
  await page.getByRole('button',{name:'Voluntariado',exact:true}).click();await page.locator('article.shift').first().getByRole('button',{name:'Cancelar minha inscrição'}).click();await expect(page.locator('article.shift').first().getByRole('button',{name:'Quero participar'})).toBeVisible();
  await volunteerContext.close();await adminContext.close();
});
test('relatório é salvo e permanece após recarregar',async({page})=>{
  await login(page);await page.getByRole('button',{name:'Relatórios',exact:true}).click();await page.getByRole('button',{name:'+ Salvar relatório'}).click();const title='Balanço E2E '+Date.now();await page.getByLabel('Título do relatório').fill(title);await page.getByRole('dialog').getByRole('button',{name:'Salvar',exact:true}).click();await expect(page.getByRole('heading',{name:title})).toBeVisible();await page.reload();await page.getByRole('button',{name:'Relatórios',exact:true}).click();await expect(page.getByRole('heading',{name:title})).toBeVisible();
});
test('layout móvel não transborda horizontalmente',async({page})=>{
  await page.setViewportSize({width:390,height:844});await login(page);await expect(page.getByRole('heading',{name:'Juntos, fazemos acontecer.'})).toBeVisible();expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBeTruthy();
});
test('conteúdo HTML cadastrado é exibido como texto, sem execução',async({page})=>{
  await login(page);await page.getByRole('button',{name:'Campanhas',exact:true}).click();await page.getByRole('button',{name:'+ Nova campanha'}).click();const text='<img src=x onerror=alert(1)> '+Date.now();await page.getByLabel('Nome da campanha').fill(text);await page.getByLabel('Propósito e descrição').fill('Validação de escape');await page.getByRole('dialog').getByRole('button',{name:'Salvar',exact:true}).click();await expect(page.getByRole('heading',{name:text})).toBeVisible();await expect(page.locator('article.campaign img')).toHaveCount(0);
});

test('relatório, download e painel separam unidades e excluem entregas estornadas',async({page})=>{
  await login(page);
  const token=await (await page.request.get('/api/csrf')).json();
  async function post(path:string,data:unknown={}) {
    const response=await page.request.post('/api'+path,{data,headers:{[token.headerName]:token.token}});
    expect(response.ok()).toBeTruthy();
    return response.json();
  }
  const suffix=Date.now();
  const campaign=await post('/campaigns',{name:'Unidades E2E '+suffix,description:'Regressão dos totais por unidade'});
  const first=await post('/items',{name:'Alimentos A '+suffix,unit:'caixa'});
  const second=await post('/items',{name:'Alimentos B '+suffix,unit:'caixa'});
  const kit=await post('/items',{name:'Higiene '+suffix,unit:'pacote'});
  await post('/campaigns/'+campaign.id+'/needs',{itemId:first.id,target:300});
  await post('/campaigns/'+campaign.id+'/publish');
  for(const [item,quantity] of [[first,120],[second,100],[kit,2]] as const)
    await post('/movements',{campaignId:campaign.id,itemId:item.id,quantity,kind:'DOACAO',reason:'Teste de unidades'});
  await post('/deliveries',{campaignId:campaign.id,itemId:first.id,quantity:3,beneficiary:'F-CAIXAS',vulnerability:2,members:2,strategy:'VULNERABILIDADE'});
  const delivery=await post('/deliveries',{campaignId:campaign.id,itemId:kit.id,quantity:1,beneficiary:'F-PACOTES',vulnerability:2,members:2,strategy:'VULNERABILIDADE'});
  await post('/deliveries/'+delivery.id+'/reverse',{reason:'Devolução de teste'});
  await page.reload();
  const stock=page.getByRole('article',{name:'Estoque por unidade'});
  const delivered=page.getByRole('article',{name:'Participação e entregas'});
  await expect(stock.locator('strong').filter({hasText:'caixa'})).toHaveText('217 caixa');
  await expect(stock.locator('strong').filter({hasText:'pacote'})).toHaveText('2 pacote');
  await expect(delivered.locator('strong').filter({hasText:'caixa'})).toHaveText('3 caixa');
  await expect(delivered.locator('strong').filter({hasText:'pacote'})).toHaveText('0 pacote');
  await page.setViewportSize({width:390,height:844});
  expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBeTruthy();
  await page.screenshot({path:'test-results/totais-por-unidade-mobile.png',fullPage:true});
  await page.setViewportSize({width:1440,height:1000});
  await page.getByRole('button',{name:'Relatórios',exact:true}).click();
  await page.getByRole('button',{name:'+ Salvar relatório'}).click();
  const title='Balanço por unidade '+suffix;
  await page.getByLabel('Título do relatório').fill(title);
  await page.getByRole('dialog').getByRole('button',{name:'Salvar',exact:true}).click();
  const card=page.locator('article.report').filter({hasText:title});
  const content=card.locator('pre');
  await expect(content).toContainText(/Doações recebidas: 220 caixa;[^\n]*2 pacote/);
  await expect(content).toContainText(/Estoque atual: 217 caixa;[^\n]*2 pacote/);
  await expect(content).toContainText(/Itens entregues: 3 caixa;[^\n]*0 pacote/);
  const saved=await content.innerText();
  const downloadPromise=page.waitForEvent('download');
  await card.getByRole('button',{name:'Baixar relatório ↓'}).click();
  const download=await downloadPromise;
  const stream=await download.createReadStream();
  const chunks:Buffer[]=[];
  for await(const chunk of stream) chunks.push(chunk);
  expect(Buffer.concat(chunks).toString('utf8')).toContain(saved);
  await post('/movements',{campaignId:campaign.id,itemId:kit.id,quantity:5,kind:'DOACAO',reason:'Depois do snapshot'});
  await page.reload();
  await page.getByRole('button',{name:'Relatórios',exact:true}).click();
  await expect(content).toHaveText(saved);
  await page.screenshot({path:'test-results/relatorio-por-unidade.png',fullPage:true});
});
