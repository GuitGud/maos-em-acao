import {chromium} from '@playwright/test';
import {mkdir} from 'node:fs/promises';
import {resolve} from 'node:path';
const directory=resolve('../docs/prototipos');await mkdir(directory,{recursive:true});
const browser=await chromium.launch({channel:process.env.PLAYWRIGHT_CHANNEL||'chrome'});
const page=await browser.newPage({viewport:{width:1440,height:1000},deviceScaleFactor:1});
const errors=[];page.on('pageerror',e=>errors.push(e.message));page.on('console',m=>{if(m.type()==='error' && /Content Security Policy|Refused to/.test(m.text())) errors.push(m.text());});
await page.goto(process.env.BASE_URL||'http://127.0.0.1:8080');
await page.getByRole('heading',{name:'Bom ter você por aqui.'}).waitFor();
await page.screenshot({path:resolve(directory,'login.png'),fullPage:true});
await page.getByLabel('E-mail',{exact:true}).fill('admin@maosemacao.local');await page.getByLabel('Senha',{exact:true}).fill('DemoGestor2026!');await page.getByRole('button',{name:'Entrar na plataforma'}).click();
await page.getByRole('heading',{name:'Juntos, fazemos acontecer.'}).waitFor();await page.screenshot({path:resolve(directory,'dashboard.png'),fullPage:true});
for(const [name,file] of [['Campanhas','campanhas'],['Estoque','estoque'],['Voluntariado','voluntariado'],['Entregas','entregas'],['Pessoas','pessoas'],['Relatórios','relatorios'],['Notificações','notificacoes']]) {
 await page.getByRole('button',{name,exact:true}).click();await page.getByRole('heading',{name,exact:true}).waitFor();await page.screenshot({path:resolve(directory,file+'.png'),fullPage:true});
}
await page.getByRole('button',{name:'Campanhas',exact:true}).click();await page.getByRole('button',{name:'+ Nova campanha'}).click();await page.getByRole('dialog').waitFor();await page.screenshot({path:resolve(directory,'nova-campanha.png'),fullPage:true});await page.getByRole('button',{name:'Fechar formulário'}).click();
await page.getByRole('button',{name:'Visão geral',exact:true}).click();await page.setViewportSize({width:390,height:844});await page.screenshot({path:resolve(directory,'mobile.png'),fullPage:true});
await browser.close();if(errors.length)throw new Error(errors.join('\n'));console.log('11 telas capturadas; sem erros JavaScript ou bloqueios CSP.');
