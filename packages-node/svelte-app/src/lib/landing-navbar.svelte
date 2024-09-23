<script>
	import { Navbar, NavBrand, NavLi, NavUl, NavHamburger, Button } from 'flowbite-svelte';
	import { goto } from '$app/navigation';
	import { page } from '$app/stores';

	$: activeUrl = $page.url.pathname;
</script>

<Navbar>
	<NavBrand href="/">
		<img src="/images/albionsquare-icon.svg" class="me-3 h-6 sm:h-9" alt="Albion Square logo" />
		<span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white"
			>Albion Square</span
		>
	</NavBrand>
	<div class="flex md:order-2">
		<Button size="sm" on:click={() => goto('/app')}>Get started</Button>
		<NavHamburger />
	</div>
	<NavUl class="order-1" {activeUrl}>
		<NavLi href="/">Features</NavLi>
		<NavLi href="/about">Feedback</NavLi>
		<NavLi href="/docs/components/navbar">Contact</NavLi>
	</NavUl>
</Navbar>
