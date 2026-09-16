import { Component, OnInit, provideZonelessChangeDetection } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';

interface Person { id:number; name:string; email:string; approved:boolean; role:string; hours:number; badge:string }
interface Campaign { id:number; name:string; description:string; status:string }
interface Item { id:number; name:string; unit:string; balance:number }
interface Need { id:number; campaignId:number; itemId:number; target:number }
interface Shift { id:number; campaignId:number; title:string; startsAt:string; endsAt:string; capacity:number; status:string }
interface Registration { id:number; shiftId:number; volunteerId:number; status:string; hours:number }
interface Movement { id:number; itemId:number; campaignId:number; quantity:number; kind:string; reason:string; createdAt:string }
interface Delivery { id:number; campaignId:number; itemId:number; quantity:number; beneficiary:string; priority:number; status:string; createdAt:string }
interface Notice { id:number; message:string; seen:boolean; createdAt:string }
interface Feedback { id:number; registrationId:number; rating:number; comment:string }
interface Report { id:number; title:string; content:string; createdAt:string }
interface State { me:Person; campaigns:Campaign[]; items:Item[]; needs:Need[]; shifts:Shift[]; registrations:Registration[]; volunteers:Person[]; movements:Movement[]; deliveries:Delivery[]; notifications:Notice[]; feedback:Feedback[]; reports:Report[]; occupancy:Record<number,number> }
type Field={ key:string; label:string; type?:string; options?:{id:string|number;name:string}[]; value?:string|number; min?:number; max?:number };
@Component({ selector:'app-root', standalone:true, imports:[CommonModule,FormsModule], templateUrl:'./app.html' })
export class App implements OnInit {
  state:State|null=null; page='Visão geral'; error=''; success=''; busy=false; loading=true; signup=false; email=''; password=''; name=''; search='';
  modal:{title:string;path:string;method:string;fields:Field[]}|null=null;
  values:Record<string,string|number>={}; csrf='';
  constructor(private cd:ChangeDetectorRef) {}
  async ngOnInit() { try { await this.token(); await this.load(); } catch {} finally { this.loading=false; this.cd.markForCheck(); } }
  get admin() { return this.state?.me.role==='ADMIN'; }
  get nav() { return ['Visão geral','Campanhas','Estoque','Voluntariado','Entregas',...(this.admin?['Pessoas','Relatórios']:[]),'Notificações','Meu perfil']; }
  get published() { return this.state?.campaigns.filter(c=>c.status==='PUBLICADA')??[]; }
  get unread() { return this.state?.notifications.filter(n=>!n.seen).length??0; }
  get totalStock() { return this.state?.items.reduce((n,i)=>n+i.balance,0)??0; }
  get totalDelivered() { return this.state?.deliveries.filter(d=>d.status==='ENTREGUE').reduce((n,d)=>n+d.quantity,0)??0; }
  get campaigns() { return this.state?.campaigns.filter(c=>c.name.toLowerCase().includes(this.search.toLowerCase()))??[]; }
  async token() { const r=await fetch('/api/csrf'); const data=await r.json(); this.csrf=data.token; }
  async request(path:string,method='GET',body?:unknown) {
    const r=await fetch('/api'+path,{method,headers:{'Content-Type':'application/json','X-XSRF-TOKEN':this.csrf},body:body===undefined?undefined:JSON.stringify(body)});
    if(!r.ok) { let msg='Não foi possível concluir. Atualize a página e tente novamente.'; try { msg=(await r.json()).message||msg; } catch {} if(r.status===401) { this.state=null; msg='Entre na sua conta para continuar.'; } throw new Error(msg); }
    return r.status===204||r.headers.get('content-length')==='0'?null: r.text().then(t=>t?JSON.parse(t):null);
  }
  async load() { this.state=await this.request('/state'); this.cd.markForCheck(); }
  async login() {
    this.busy=true; this.error=''; this.success='';
    try {
      await this.token();
      if(this.signup) { await this.request('/register','POST',{name:this.name,email:this.email,password:this.password}); this.signup=false; this.success='Cadastro enviado! Entre e aguarde a aprovação da gestão.'; }
      else {
        const r=await fetch('/api/login',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded','X-XSRF-TOKEN':this.csrf},body:new URLSearchParams({email:this.email,password:this.password})});
        if(!r.ok) throw new Error('E-mail ou senha inválidos.');
        await this.token(); await this.load(); this.password='';
      }
    } catch(e) { this.error=(e as Error).message; } finally { this.busy=false;this.cd.markForCheck(); }
  }
  async logout() { await this.run('/logout','POST',undefined,false); this.state=null; await this.token(); this.cd.markForCheck(); }
  async run(path:string,method='POST',body?:unknown,reload=true) {
    if(this.busy) return; this.busy=true;this.error='';this.success='';
    try { await this.request(path,method,body); this.modal=null; if(reload) await this.load(); this.success='Pronto! Alteração salva com sucesso.'; }
    catch(e) { this.error=(e as Error).message; }
    finally { this.busy=false; this.cd.markForCheck(); }
  }
  navigate(page:string) { this.page=page;this.error='';this.success='';this.search=''; }
  open(title:string,path:string,fields:Field[],method='POST') { this.error='';this.modal={title,path,fields,method};this.values={};fields.forEach(f=>this.values[f.key]=f.value??f.options?.[0]?.id??''); }
  submit() {
    if(!this.modal) return;
    const body={...this.values};
    for(const f of this.modal.fields) {
      if(f.type==='number'||f.type==='select-number') body[f.key]=Number(body[f.key]);
      if(f.type==='datetime-local') body[f.key]=new Date(body[f.key]).toISOString().slice(0,19);
    }
    this.run(this.modal.path,this.modal.method,body);
  }
  campaignName(id:number) { return this.state?.campaigns.find(c=>c.id===id)?.name??'Campanha'; }
  itemName(id:number) { return this.state?.items.find(c=>c.id===id)?.name??'Item'; }
  personName(id:number) { return this.state?.volunteers.find(c=>c.id===id)?.name??'Voluntário'; }
  shiftName(id:number) { return this.state?.shifts.find(c=>c.id===id)?.title??'Turno'; }
  needs(id:number) { return this.state?.needs.filter(n=>n.campaignId===id)??[]; }
  received(n:Need) { return this.state?.movements.filter(m=>m.campaignId===n.campaignId&&m.itemId===n.itemId&&m.kind==='DOACAO').reduce((s,m)=>s+m.quantity,0)??0; }
  progress(n:Need) { return Math.min(100,100*this.received(n)/n.target); }
  myRegistration(s:Shift) { return this.state?.registrations.find(r=>r.shiftId===s.id && r.volunteerId===this.state?.me.id && r.status==='INSCRITO'); }
  registrations(id:number) { return this.state?.registrations.filter(r=>r.shiftId===id)??[]; }
  completed(id:number) { return this.state?.shifts.find(s=>s.id===id)?.status==='CONCLUIDO'; }
  date(value:string) { return new Date(value+'Z').toLocaleString('pt-BR',{dateStyle:'short',timeStyle:'short'}); }
  local(value:string) { const d=new Date(value+'Z'); return new Date(d.getTime()-d.getTimezoneOffset()*60000).toISOString().slice(0,16); }
  campaignForm(c?:Campaign) { this.open(c?'Editar campanha':'Nova campanha','/campaigns'+(c?'/'+c.id:''),[{key:'name',label:'Nome da campanha',value:c?.name,max:120},{key:'description',label:'Propósito e descrição',type:'textarea',value:c?.description,max:2000}],c?'PUT':'POST'); }
  itemForm() { this.open('Cadastrar item','/items',[{key:'name',label:'Nome do item',max:100},{key:'unit',label:'Unidade (kg, unidade, kit)',max:30}]); }
  itemField():Field { return {key:'itemId',label:'Item',type:'select-number',options:this.state?.items.map(i=>({id:i.id,name:i.name+' · '+i.unit}))}; }
  campaignField():Field { return {key:'campaignId',label:'Campanha',type:'select-number',options:this.published.map(c=>({id:c.id,name:c.name}))}; }
  needForm(c:Campaign) { this.open('Definir necessidade','/campaigns/'+c.id+'/needs',[this.itemField(),{key:'target',label:'Meta total (substitui a anterior)',type:'number',min:1}]); }
  moveForm(adjust=false) { this.open(adjust?'Ajustar estoque':'Registrar doação','/movements',[this.campaignField(),this.itemField(),{key:'quantity',label:adjust?'Quantidade (+ entrada / − saída)':'Quantidade recebida',type:'number',min:adjust?undefined:1},{key:'kind',label:'Movimentação',type:'select',options:[{id:adjust?'AJUSTE':'DOACAO',name:adjust?'Ajuste justificado':'Doação'}]},{key:'reason',label:'Origem / justificativa',max:500}]); }
  shiftForm(s?:Shift) { this.open(s?'Editar turno':'Novo turno','/shifts'+(s?'/'+s.id:''),[...(s?[]:[this.campaignField()]),{key:'title',label:'Atividade do turno',value:s?.title,max:120},{key:'startsAt',label:'Início (horário local)',type:'datetime-local',value:s?this.local(s.startsAt):''},{key:'endsAt',label:'Fim (horário local)',type:'datetime-local',value:s?this.local(s.endsAt):''},{key:'capacity',label:'Número de vagas',type:'number',min:1,max:1000,value:s?.capacity??10}],s?'PUT':'POST'); }
  deliveryForm() { this.open('Registrar entrega','/deliveries',[this.campaignField(),this.itemField(),{key:'quantity',label:'Quantidade',type:'number',min:1},{key:'beneficiary',label:'Código da família / instituição',max:150},{key:'vulnerability',label:'Vulnerabilidade (1 a 5)',type:'number',min:1,max:5,value:3},{key:'members',label:'Pessoas atendidas',type:'number',min:1,max:100,value:1},{key:'strategy',label:'Critério da pontuação de prioridade',type:'select',options:[{id:'VULNERABILIDADE',name:'Maior vulnerabilidade'},{id:'TAMANHO_FAMILIA',name:'Maior família'}]}]); }
  reverse(d:Delivery) { this.open('Estornar entrega #'+d.id,'/deliveries/'+d.id+'/reverse',[{key:'reason',label:'Justificativa do estorno',max:500}]); }
  feedbackForm(r:Registration) { const old=this.state?.feedback.find(f=>f.registrationId===r.id);this.open('Avaliar participação','/feedback',[{key:'registrationId',label:'Participação',type:'select-number',options:[{id:r.id,name:this.shiftName(r.shiftId)}]},{key:'rating',label:'Nota (1 a 5)',type:'number',min:1,max:5,value:old?.rating??5},{key:'comment',label:'Como foi sua experiência?',type:'textarea',value:old?.comment,max:1000}]); }
  reportForm() { this.open('Salvar relatório','/reports',[{key:'title',label:'Título do relatório',value:'Balanço de solidariedade',max:120}]); }
  profileForm() { this.open('Editar meu nome','/profile',[{key:'name',label:'Nome',value:this.state?.me.name,max:100}],'PUT'); }
  download(r:Report) { const url=URL.createObjectURL(new Blob([r.title+'\n'+this.date(r.createdAt)+'\n\n'+r.content],{type:'text/plain;charset=utf-8'}));const a=document.createElement('a');a.href=url;a.download='relatorio-'+r.id+'.txt';a.click();URL.revokeObjectURL(url); }
}
bootstrapApplication(App,{providers:[provideZonelessChangeDetection()]}).catch(console.error);
